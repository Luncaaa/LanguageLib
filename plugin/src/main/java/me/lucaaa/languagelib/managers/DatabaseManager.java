package me.lucaaa.languagelib.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.Database;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.lucaaa.languagelib.managers.messages.PluginMessagesManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;

// Maybe a player data is being saved while plugin is shut down
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DatabaseManager {
    private final LanguageLib plugin;
    private final CompletableFuture<Void> initFuture;
    private final Set<CompletableFuture<Void>> pendingOperations = ConcurrentHashMap.newKeySet();
    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(5);

    private HikariDataSource dataSource;
    private volatile boolean isShuttingDown = false;

    public DatabaseManager(LanguageLib plugin) {
        this.plugin = plugin;

        CompletableFuture<Void> tracker = new CompletableFuture<>();
        pendingOperations.add(tracker);

        this.initFuture = CompletableFuture.runAsync(this::initializeDatabase, dbExecutor)
                .whenComplete((result, e) -> {
                    if (e != null) plugin.logError(Level.SEVERE, "The database couldn't be loaded! Players won't see their saved languages.", e);

                    tracker.complete(null);
                    pendingOperations.remove(tracker);
                });
    }

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
                if (!dbFile.exists()) {
                    dbFile.mkdirs();
                    dbFile.createNewFile();
                }
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

        String query = "CREATE TABLE IF NOT EXISTS player_data(uuid VARCHAR(36) PRIMARY KEY, lang VARCHAR(32))";
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

        CompletableFuture<Void> tracker = new CompletableFuture<>();
        pendingOperations.add(tracker);

        initFuture.thenRunAsync(() -> {
            PluginMessagesManager messagesManager = plugin.getPluginMessagesManager();

            try {
                String language = getOrCreatePlayerLanguage(playerData.getUuid(), playerData.getLocale());

                LanguageImpl lang = messagesManager.get(language, false);
                if (lang == null) {
                    playerData.setLang(messagesManager.getDefaultLang(), true);
                } else {
                    playerData.setLang(lang, true);
                }
            } catch (SQLException e) {
                plugin.logError(Level.SEVERE, "Failed to load data for player " + playerData.getName(), e);
                // Set default language on error
                playerData.setLang(messagesManager.getDefaultLang(), true);
            }

        }, dbExecutor).whenComplete((result, e) -> {
            tracker.complete(null);
            pendingOperations.remove(tracker);
        });
    }

    /**
     * Gets the player's language or creates a new entry in the database if it doesn't exist
     */
    private String getOrCreatePlayerLanguage(UUID uuid, String locale) throws SQLException {
        String query = "SELECT lang FROM player_data WHERE uuid = ?";
        String uuidStr = uuid.toString();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, uuidStr);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("lang");
                }
            }
        }

        // If this code is reached (nothing was returned), the player doesn't exist.
        String language = (plugin.getMainConfig().usePlayerLocale) ? locale + ".yml" : plugin.getPluginMessagesManager().getDefaultLang().getFileName();

        String query1 = "INSERT INTO player_data (uuid, lang) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query1)) {
            statement.setString(1, uuidStr);
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

        CompletableFuture<Void> tracker = new CompletableFuture<>();
        pendingOperations.add(tracker);

        initFuture.thenRunAsync(() -> savePlayerDataSync(playerData), dbExecutor)
                .whenComplete((result, e) -> {
                    tracker.complete(null);
                    pendingOperations.remove(tracker);
                });
    }

    /**
     * Saves player data synchronously (used during shutdown)
     */
    private void savePlayerDataSync(PlayerData playerData) {
        String uuidStr = playerData.getUuid().toString();
        String langFileName = playerData.getLang().getFileName();
        boolean useMySQL = plugin.getMainConfig().database.useMySQL;

        // UPSERT depending on whether the plugin is using MySQL or SQLite
        String query;
        if (plugin.getMainConfig().database.useMySQL) {
            query = "INSERT INTO player_data (uuid, lang) VALUES (?, ?) ON DUPLICATE KEY UPDATE lang = ?";
        } else {
            query = "INSERT INTO player_data (uuid, lang) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET lang = excluded.lang";
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, uuidStr);
            statement.setString(2, langFileName);

            // Pass the language again for the UPDATE part of MySQL
            if (useMySQL) {
                statement.setString(3, langFileName);
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "Failed to save data for player " + playerData.getName(), e);
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
     * Always runs sync because:
     *  - If plugin is reloading, shutting down sync will make the new instance of the database manager have the updated data.
     *  - If server is closing, it must be run async anyway.
     */
    public void shutdown() {
        isShuttingDown = true;

        try {
            waitForPendingOperations().get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            plugin.logError(Level.WARNING, "Timeout waiting for database operations to complete", e);
        } finally {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }

            dbExecutor.shutdown();
            try {
                dbExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                plugin.logError(Level.WARNING, "Database executor interrupted while shutting down", e);
            }
        }
    }
}