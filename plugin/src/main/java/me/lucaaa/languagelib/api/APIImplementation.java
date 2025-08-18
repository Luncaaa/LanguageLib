package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.managers.messages.MessagesManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class APIImplementation implements LanguageAPI {
    private final MessagesManager messagesManager;

    public APIImplementation(LanguageLib languageLib, JavaPlugin plugin, String prefix, String languagesFolderPath) {
        this.messagesManager = new MessagesManagerImpl(languageLib, plugin, false, prefix, languagesFolderPath);
    }

    @Override
    public void reload() {
        messagesManager.reload();
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void onLeave(Player player) {
        ((MessagesManagerImpl) messagesManager).removeMessageable(player);
    }
}