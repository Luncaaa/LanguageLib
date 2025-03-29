package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Language;
import org.bukkit.entity.Player;

public class PlayerData implements LangProvider {
    private final LanguageLib plugin;
    private final Player player;
    private Language lang;

    public PlayerData(LanguageLib plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        plugin.getDatabaseManager().loadPlayerData(this);
    }

    // ---[ Messages ]---
    @Override
    public Language getLang() {
        return lang;
    }

    public void setLang(Language language, boolean save) {
        this.lang = language;
        if (save) plugin.getDatabaseManager().savePlayerData(this);
    }
    // -----

    // ---[ Database ]---
    public String getPlayerName() {
        return player.getName();
    }

    public Player getPlayer() {
        return player;
    }
    // -----
}