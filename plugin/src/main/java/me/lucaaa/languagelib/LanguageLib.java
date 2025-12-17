package me.lucaaa.languagelib;

import me.lucaaa.languagelib.api.APIProvider;
import me.lucaaa.languagelib.api.APIProviderImplementation;
import me.lucaaa.languagelib.api.events.PluginReloadEvent;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.commands.LanguageCommand;
import me.lucaaa.languagelib.common.HeadParser;
import me.lucaaa.languagelib.common.Logger;
import me.lucaaa.languagelib.data.ServerConsole;
import me.lucaaa.languagelib.data.configs.MainConfig;
import me.lucaaa.languagelib.listeners.*;
import me.lucaaa.languagelib.managers.*;
import me.lucaaa.languagelib.managers.messages.PluginMessagesManager;
import me.lucaaa.languagelib.managers.messages.ServerMessagesManager;
import me.lucaaa.languagelib.v1_13_R2.LegacyHeadParser;
import me.lucaaa.languagelib.v1_18_R1.ModernHeadParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class LanguageLib extends JavaPlugin implements Logger {
    private boolean isRunning = false;
    private boolean useNewHeads;
    private boolean isPapiInstalled;
    private ServerConsole serverConsole;

    // Config files.
    private MainConfig mainConfig;
    private HeadParser headParser;

    // Managers.
    private DatabaseManager databaseManager;
    private ItemsManager itemsManager;
    private PluginMessagesManager pluginMessagesManager;
    private ServerMessagesManager serverMessagesManager;
    private PlayersManager playersManager;
    private InventoriesManager inventoriesManager;

    // API & Other.
    private APIProviderImplementation apiProvider;
    private BukkitAudiences audiences;

    // Reload the config files.
    public void reloadConfigs(Messageable reloader) {
        this.isPapiInstalled = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        mainConfig = new MainConfig(this);

        // Managers
        if (!isRunning) {
            initManagers();

        } else {
            if (inventoriesManager != null) {
                inventoriesManager.shutdown();
            }

            databaseManager.shutdown(false);
            initManagers();

            // Once all managers have been loaded (including database), send success message if applicable.
            if (reloader != null) {
                // getManager(PluginMessagesManager.class) is used instead of reloader.sendMessage() so that the newly created
                // language files are used instead of the ones before reloading (they might have changes).
                pluginMessagesManager.sendMessage(reloader, "commands.reload.success", null);
            }

            getServer().getPluginManager().callEvent(new PluginReloadEvent());
        }
    }

    private void initManagers() {
        headParser = useNewHeads ? new ModernHeadParser(this) : new LegacyHeadParser(this);
        databaseManager = new DatabaseManager(this);

        itemsManager = new ItemsManager(this);

        if (isRunning) {
            pluginMessagesManager.reload();
            serverMessagesManager.reload();
            playersManager.reload();

        } else {
            pluginMessagesManager = new PluginMessagesManager(this);
            serverMessagesManager = new ServerMessagesManager(this);
            playersManager = new PlayersManager(this);
        }

        inventoriesManager = new InventoriesManager(this);

        // API must be reloaded after previous managers have been reloaded.
        if (isRunning) {
            apiProvider.reload();
        }
    }

    @Override
    public void onEnable() {
        useNewHeads = useNewHeads(getServer().getBukkitVersion().split("-")[0]); // 1.18.1 onwards.

        // Set up files and managers.
        reloadConfigs(null);

        // Set up server console.
        serverConsole = new ServerConsole(this);

        // Register listeners.
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListeners(this), this);

        // Registers the main command and adds tab completions.
        Objects.requireNonNull(this.getCommand("language")).setExecutor(new LanguageCommand(this));

        // Enables the API.
        apiProvider = new APIProviderImplementation(this);
        APIProvider.setImplementation(apiProvider);
        audiences = BukkitAudiences.create(this);

        // Add PAPI support.
        if (isPapiInstalled) {
            new PlaceholdersManager(this).register();
        }

        isRunning = true;
        pluginMessagesManager.sendMessage(getServer().getConsoleSender(), "&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        if (inventoriesManager != null) inventoriesManager.shutdown();
        if (databaseManager != null) databaseManager.shutdown(true);
        if (audiences != null) audiences.close();
        isRunning = false;
    }

    private boolean useNewHeads(String version) {
        String[] versionDivided = version.split("\\.");
        int versionMajor = Integer.parseInt(versionDivided[1]);
        int versionMinor = (versionDivided.length > 2) ? Integer.parseInt(versionDivided[2]) : 0;

        if (versionMajor == 18) {
            return versionMinor >= 1;
        } else {
            return versionMajor > 18;
        }
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        getLogger().log(level, message, error);
    }

    public boolean isPapiInstalled() {
        return this.isPapiInstalled;
    }

    public ServerConsole getServerConsole() {
        return serverConsole;
    }

    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    public HeadParser getHeadParser() {
        return headParser;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public PluginMessagesManager getPluginMessagesManager() {
        return pluginMessagesManager;
    }

    public ServerMessagesManager getServerMessagesManager() {
        return serverMessagesManager;
    }

    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    public InventoriesManager getInventoriesManager() {
        return inventoriesManager;
    }

    public APIProviderImplementation getApiProvider() {
        return apiProvider;
    }

    public Audience getAudience(Player player) {
        return audiences.player(player);
    }

    public Audience getConsoleAudience() {
        return audiences.console();
    }
}