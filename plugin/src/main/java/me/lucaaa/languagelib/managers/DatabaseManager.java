package me.lucaaa.languagelib.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.Database;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.lucaaa.languagelib.managers.messages.PluginMessagesManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.logging.Level;

// Maybe a player data is being saved while plugin is shut down
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DatabaseManager {
    private final LanguageLib plugin;
    private final ConcurrentLinkedQueue<CompletableFuture<Void>> pendingOperations;

    private HikariDataSource dataSource;
    private volatile boolean isShuttingDown = false;

    public DatabaseManager(LanguageLib plugin) {
        this.plugin = plugin;
        this.pendingOperations = new ConcurrentLinkedQueue<>();

        initializeDatabase();
    }

    /**
     * Should probably be run async
     */
    private void initializeDatabase() {
        String url;
        String user;
        String password;

        boolean useMySQL = plugin.getMainConfig().database.useMySQL;
        if (!useMySQL) {
            user = null;
            password = null;
            File dbFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "playerdata.db");
            try {
                if (!dbFile.exists()) dbFile.createNewFile();
            } catch (IOException e) {
                plugin.logError(Level.WARNING, "An error occurred while creating the database file.", e);
            }
            url = "jdbc:sqlite:"+ dbFile.getAbsolutePath();

        } else {
            Database database = plugin.getMainConfig().database;
            String host = database.host;
            String port = database.port;
            user = database.username;
            password = database.password;
            String dbName = database.name;
            url = "jdbc:mysql://"+host+":"+port+"/"+dbName;
        }

        setupPool(useMySQL, url, user, password);

        String query = "CREATE TABLE IF NOT EXISTS player_data(name TINYTEXT, lang TINYTEXT)";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "An error occurred while creating the stats table.", e);
        }
    }

    private void setupPool(boolean useMysql, String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);

        if (useMysql) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(username);
            config.setPassword(password);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        } else {
            config.setDriverClassName("org.sqlite.JDBC");
            config.addDataSourceProperty("journal_mode", "WAL");
            config.addDataSourceProperty("synchronous", "NORMAL");
        }

        config.setMinimumIdle(1);
        config.setMaximumPoolSize(25);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
    }


    private Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is not available");
        }
        return dataSource.getConnection();
    }

    /**
     * Loads player data asynchronously
     */
    public void loadPlayerData(PlayerData playerData) {
        if (isShuttingDown) {
            return;
        }

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            String playerName = playerData.getPlayer().getName();
            PluginMessagesManager messagesManager = plugin.getPluginMessagesManager();

            try {
                String language = getOrCreatePlayerLanguage(playerName, playerData.getPlayer());

                LanguageImpl lang = messagesManager.get(language, false);
                if (lang == null) {
                    playerData.setLang(messagesManager.getDefaultLang(), true);
                } else {
                    playerData.setLang(lang, true);
                }
            } catch (SQLException e) {
                plugin.logError(Level.SEVERE, "Failed to load data for player " + playerName, e);
                // Set default language on error
                playerData.setLang(messagesManager.getDefaultLang(), true);
            }
        });

        pendingOperations.add(future);
        future.whenComplete((v, t) -> pendingOperations.remove(future));
    }

    /**
     * Gets the player's language or creates a new entry in the database if it doesn't exist
     */
    private String getOrCreatePlayerLanguage(String playerName, Player player) throws SQLException {
        String query = "SELECT lang FROM player_data WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, playerName);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("lang");
                }
            }
        }

        // If this code is reached (nothing was returned), the player doesn't exist.
        String language = (plugin.getMainConfig().usePlayerLocale) ? player.getLocale() + ".yml" : plugin.getPluginMessagesManager().getDefaultLang().getFileName();

        String query1 = "INSERT INTO player_data (name, lang) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query1)) {
            statement.setString(1, playerName);
            statement.setString(2, language);
            statement.executeUpdate();
        }

        return language;
    }

    /**
     * Saves player data asynchronously
     */
    public void savePlayerData(PlayerData playerData) {
        if (isShuttingDown) {
            // During shutdown, save synchronously
            savePlayerDataSync(playerData);
            return;
        }

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> savePlayerDataSync(playerData));

        pendingOperations.add(future);
        future.whenComplete((v, t) -> pendingOperations.remove(future));
    }

    /**
     * Saves player data synchronously (used during shutdown)
     */
    private void savePlayerDataSync(PlayerData playerData) {
        String playerName = playerData.getPlayer().getName();

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE player_data SET lang = ? WHERE name = ?")) {
            statement.setString(1, playerData.getLang().getFileName());
            statement.setString(2, playerName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                // If the player doesn't exist, create a new database entry.
                String query = "INSERT INTO player_data (name, lang) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(query)) {
                    insertStmt.setString(1, playerName);
                    insertStmt.setString(2, playerData.getLang().getFileName());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "Failed to save data for player " + playerName, e);
        }
    }

    /**
     * Waits for all pending operations to complete
     */
    public CompletableFuture<Void> waitForPendingOperations() {
        if (pendingOperations.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<?>[] futures = pendingOperations.toArray(new CompletableFuture[0]);
        return CompletableFuture.allOf(futures);
    }

    /**
     * Shut down database manager.
     */
    public void shutdown(boolean sync) {
        isShuttingDown = true;

        if (sync) {
            try {
                waitForPendingOperations().get(30, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                plugin.logError(Level.WARNING, "Timeout waiting for database operations to complete", e);
            }

            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }

        } else {
            waitForPendingOperations().thenRunAsync(() -> {
                // Close existing pool
                if (dataSource != null && !dataSource.isClosed()) {
                    dataSource.close();
                }
            });
        }
    }
}