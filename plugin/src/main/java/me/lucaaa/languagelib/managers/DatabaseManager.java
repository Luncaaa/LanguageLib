package me.lucaaa.languagelib.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.Database;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.Language;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DatabaseManager {
    private final LanguageLib plugin;
    private final Map<String, CompletableFuture<Void>> savingData = new HashMap<>();
    private final CompletableFuture<Void> dataSourceInit;

    // Connection pool
    private HikariDataSource dataSource;

    public DatabaseManager(LanguageLib plugin, boolean useMySQL) {
        this.plugin = plugin;

        String url;
        String user;
        String password;

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

        this.dataSourceInit = CompletableFuture.runAsync(() -> {
            setupPool(useMySQL, url, user, password);

            try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS player_data(name TINYTEXT, lang TINYTEXT)")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.logError(Level.SEVERE, "An error occurred while creating the stats table.", e);
            }
        });
    }

    private void setupPool(boolean useMysql, String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        if (useMysql) {
            config.setJdbcUrl(url);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(username);
            config.setPassword(password);
        } else {
            config.setJdbcUrl(url);
            config.setDriverClassName("org.sqlite.JDBC");
        }
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(60000);
        config.setConnectionTestQuery("SELECT 1");
        dataSource = new HikariDataSource(config);
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "An error occurred while getting a database connection. Data won't be updated!");
            throw new RuntimeException(e);
        }
    }

    private boolean playerExists(String playerName) {
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("SELECT * FROM player_data WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet results = statement.executeQuery();

            boolean exists = results.next();
            results.close();
            return exists;

        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "An error occurred while checking if player " + playerName + " exists.", e);
            return false;
        }
    }

    private void createPlayer(String playerName, String locale) {
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("INSERT INTO player_data VALUES ('"+playerName+"', '"+locale+"')")) {
            statement.executeUpdate();

        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "An error occurred while creating stats for player " + playerName, e);
        }
    }

    private String getLang(String playerName) {
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("SELECT * FROM player_data WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet query = statement.executeQuery();
            query.next();
            String result = query.getString("lang");
            query.close();
            return result;
        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "An error occurred while getting data \"lang\" for player " + playerName, e);
            return "Database Error";
        }
    }

    private void updateString(String playerName, String newValue) {
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("UPDATE player_data SET lang = ? WHERE name = ?")) {
            statement.setString(1, newValue);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.logError(Level.SEVERE, "An error occurred while saving data \"lang\" for player " + playerName, e);
        }
    }

    private CompletableFuture<Void> isSaving(String playerName) {
        return savingData.get(playerName);
    }

    private void addSaving(String playerName, CompletableFuture<Void> function) {
        function.thenRun(() -> savingData.remove(playerName));
        savingData.put(playerName, function);
    }

    public void loadPlayerData(PlayerData playerData) {
        String playerName = playerData.getPlayerName();
        MessagesManagerImpl messagesManager = plugin.getManager(MessagesManagerImpl.class);

        Runnable load = () -> {
            boolean exists = playerExists(playerName);
            Player player = (Player) playerData.getSender();

            if (!exists) {
                String language;
                if (plugin.getMainConfig().usePlayerLocale) {
                    language = player.getLocale() + ".yml";
                } else {
                    language = messagesManager.getDefaultLang().getFileName();
                }
                createPlayer(playerName, language);
            }

            Language lang = messagesManager.get(getLang(playerName));
            if (lang == null) {
                playerData.setLang(messagesManager.getDefaultLang());
            } else {
                playerData.setLang(lang);
            }
        };

        if (!dataSourceInit.isDone()) {
            dataSourceInit.thenRun(load);
        } else {
            CompletableFuture<Void> saving = isSaving(playerName);
            if (saving == null) {
                CompletableFuture.runAsync(load);
            } else {
                saving.thenRun(load);
            }
        }
    }

    public void savePlayerData(PlayerData playerData, boolean async) {
        String playerName = playerData.getPlayerName();

        Runnable task = () -> updateString(playerName, playerData.getLang().getFileName());

        if (async) {
            addSaving(playerName, CompletableFuture.runAsync(task));
        } else {
            task.run();
        }
    }
}