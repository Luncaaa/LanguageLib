package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData extends MessageableImpl {
    private final LanguageLib plugin;
    private final Player player;
    private Language lang;

    public PlayerData(LanguageLib plugin, Player player) {
        super(player, plugin.getManager(MessagesManagerImpl.class));
        this.plugin = plugin;
        this.player = player;

        plugin.getDatabaseManager().loadPlayerData(this);
    }

    // ---[ Messages ]---
    @Override
    public Language getLang() {
        return lang;
    }

    public void setLang(Language language) {
        this.lang = language;
        plugin.getDatabaseManager().savePlayerData(this);
    }

    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%language%", lang.getName());
        placeholders.put("%language_code%", lang.getCode());
        return placeholders;
    }
    // -----

    // ---[ Database ]---
    public String getPlayerName() {
        return player.getName();
    }
    // -----
}