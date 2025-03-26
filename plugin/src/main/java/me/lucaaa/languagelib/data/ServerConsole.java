package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Map;

public class ServerConsole implements MessageableImpl {
    private final LanguageLib plugin;
    private final ConsoleCommandSender sender;

    public ServerConsole(LanguageLib plugin, ConsoleCommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message, boolean addPrefix) {
        plugin.getManager(MessagesManagerImpl.class).sendMessage(sender, message, addPrefix);
    }

    @Override
    public void sendMessage(String key, Map<String, String> placeholders, boolean addPrefix) {
        plugin.getManager(MessagesManagerImpl.class).sendMessage(this, key, placeholders, addPrefix);
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public Language getLang() {
        return plugin.getManager(MessagesManagerImpl.class).getDefaultLang();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }
}