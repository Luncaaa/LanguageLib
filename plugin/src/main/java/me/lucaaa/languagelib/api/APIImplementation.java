package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class APIImplementation implements LanguageAPI {
    private final LanguageLib languageLib;
    private final Plugin plugin;
    private final String prefix;
    private final String languagesFolderPath;

    private MessagesManager messagesManager;

    public APIImplementation(LanguageLib languageLib, Plugin plugin, String prefix, String languagesFolderPath) {
        this.languageLib = languageLib;
        this.plugin = plugin;
        this.prefix = prefix;
        this.languagesFolderPath = languagesFolderPath;

        this.messagesManager = new MessagesManagerImpl(languageLib, plugin.getDataFolder().getAbsolutePath(), prefix, languagesFolderPath, false);
    }

    /**
     * Forces the plugin to create a new MessagesManager.
     */
    public void reload() {
        this.messagesManager = new MessagesManagerImpl(languageLib, plugin.getDataFolder().getAbsolutePath(), prefix, languagesFolderPath, false);
    }

    public void onLeave(Player player) {
        ((MessagesManagerImpl) messagesManager).onLeave(player);
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }
}