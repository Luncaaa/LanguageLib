package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class APIImplementation implements LanguageAPI {
    private final MessagesManager messagesManager;

    public APIImplementation(LanguageLib languageLib, Plugin plugin, String prefix, String languagesFolderPath) {
        this.messagesManager = new MessagesManagerImpl(languageLib, plugin.getDataFolder().getAbsolutePath(), prefix, plugin.getDataFolder().getAbsolutePath() + File.separator + languagesFolderPath, false);
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    @Override
    public Messageable getMessageable(CommandSender sender) {
        return messagesManager.getMessageable(sender);
    }
}
