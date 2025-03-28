package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.command.ConsoleCommandSender;

public class ServerConsole implements LangProvider {
    private final LanguageLib plugin;

    public ServerConsole(LanguageLib plugin, ConsoleCommandSender sender) {
        this.plugin = plugin;
    }

    @Override
    public Language getLang() {
        return plugin.getManager(MessagesManagerImpl.class).getDefaultLang();
    }
}