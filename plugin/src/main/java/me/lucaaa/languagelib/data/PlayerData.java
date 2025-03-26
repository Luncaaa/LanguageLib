package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements MessageableImpl {
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
    public void sendMessage(String message, boolean addPrefix) {
        plugin.getManager(MessagesManagerImpl.class).sendMessage(player, message, addPrefix);
    }

    @Override
    public void sendMessage(String key, Map<String, String> placeholders, boolean addPrefix) {
        plugin.getManager(MessagesManagerImpl.class).sendMessage(this, key, placeholders, addPrefix);
    }

    @Override
    public CommandSender getSender() {
        return player;
    }

    @Override
    public Language getLang() {
        return lang;
    }

    public void setLang(Language language) {
        this.lang = language;
    }

    @Override
    public boolean isPlayer() {
        return true;
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

    public void saveData(boolean async) {
        plugin.getDatabaseManager().savePlayerData(this, async);
    }
    // -----
}