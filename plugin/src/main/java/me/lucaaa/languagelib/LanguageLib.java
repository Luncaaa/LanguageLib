package me.lucaaa.languagelib;

import me.lucaaa.languagelib.api.APIProvider;
import me.lucaaa.languagelib.api.APIProviderImplementation;
import me.lucaaa.languagelib.api.events.PluginReloadEvent;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.commands.LanguageCommand;
import me.lucaaa.languagelib.data.ServerConsole;
import me.lucaaa.languagelib.data.configs.MainConfig;
import me.lucaaa.languagelib.listeners.*;
import me.lucaaa.languagelib.managers.*;
import me.lucaaa.languagelib.managers.messages.PluginMessagesManager;
import me.lucaaa.languagelib.managers.messages.ServerMessagesManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class LanguageLib extends JavaPlugin {
    private boolean isRunning = false;
    private boolean useNewHeads;
    private boolean isPapiInstalled;
    private ServerConsole serverConsole;

    // Config files.
    private MainConfig mainConfig;

    // Managers.
    private final Map<Class<? extends Manager<?, ?>>, Manager<?, ?>> managers = new HashMap<>();
    private DatabaseManager databaseManager;

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
            if (managers.containsKey(InventoriesManager.class)) {
                getManager(InventoriesManager.class).shutdown();
            }

            databaseManager.shutdown(false);
            initManagers();

            // Once all managers have been loaded (including database), send success message if applicable.
            if (reloader != null) {
                // getManager(PluginMessagesManager.class) is used instead of reloader.sendMessage() so that the newly created
                // language files are used instead of the ones before reloading (they might have changes).
                getManager(PluginMessagesManager.class).sendMessage(reloader, "commands.reload.success", null);
            }

            getServer().getPluginManager().callEvent(new PluginReloadEvent());
        }
    }

    private void initManagers() {
        databaseManager = new DatabaseManager(this);

        managers.put(ItemsManager.class, new ItemsManager(this, useNewHeads));

        if (isRunning) {
            getManager(PluginMessagesManager.class).reload();
            getManager(ServerMessagesManager.class).reload();
            getManager(PlayersManager.class).reload();

        } else {
            managers.put(PluginMessagesManager.class, new PluginMessagesManager(this));
            managers.put(ServerMessagesManager.class, new ServerMessagesManager(this));
            managers.put(PlayersManager.class, new PlayersManager(this));
        }

        managers.put(InventoriesManager.class, new InventoriesManager(this));

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
        getManager(PluginMessagesManager.class).sendMessage(getServer().getConsoleSender(), "&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        getManager(InventoriesManager.class).shutdown();
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

    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

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

    @SuppressWarnings("unchecked")
    public <T extends Manager<?, ?>> T getManager(Class<T> manager) {
        return (T) managers.get(manager);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
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