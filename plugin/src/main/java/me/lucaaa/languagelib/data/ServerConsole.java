package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;

public class ServerConsole implements LangProvider {
    private final LanguageLib plugin;

    public ServerConsole(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public LanguageImpl getLang() {
        return plugin.getManager(MessagesManagerImpl.class).getDefaultLang();
    }
}