package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class APIImplementation implements LanguageAPI {
    private final LanguageLib languageLib;
    private final JavaPlugin plugin;
    private final String prefix;
    private final String languagesFolderPath;

    private MessagesManager messagesManager;

    public APIImplementation(LanguageLib languageLib, JavaPlugin plugin, String prefix, String languagesFolderPath) {
        this.languageLib = languageLib;
        this.plugin = plugin;
        this.prefix = prefix;
        this.languagesFolderPath = languagesFolderPath;

        this.messagesManager = new MessagesManagerImpl(languageLib, plugin, prefix, languagesFolderPath);
    }

    /**
     * Forces the plugin to create a new MessagesManager.
     */
    @Override
    public void reload() {
        this.messagesManager = new MessagesManagerImpl(languageLib, plugin, prefix, languagesFolderPath);
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void onLeave(Player player) {
        ((MessagesManagerImpl) messagesManager).onLeave(player);
    }
}