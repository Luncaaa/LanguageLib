package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.events.AsyncPlayerLanguageLoadEvent;
import me.lucaaa.languagelib.api.events.LanguageEvent;
import me.lucaaa.languagelib.api.events.PlayerLanguageChangeEvent;
import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;

public class PlayerData implements LangProvider {
    private final LanguageLib plugin;
    private final Player player;
    private Language lang;

    public PlayerData(LanguageLib plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        // Set language to default until it's loaded.
        this.lang = plugin.getPluginMessagesManager().getDefaultLang();

        plugin.getDatabaseManager().loadPlayerData(this);
    }

    // ---[ Messages ]---
    @Override
    public Language getLang() {
        return lang;
    }

    /**
     * Sets the player's language.
     * @param language The language to set.
     * @param onJoin Whether this method was called when the player joined or the player set his language manually.
     */
    public void setLang(Language language, boolean onJoin) {
        Language oldLang = lang;
        this.lang = language;

        LanguageEvent event;
        if (onJoin) {
            event = new AsyncPlayerLanguageLoadEvent(player, language);
        } else {
            plugin.getDatabaseManager().savePlayerData(this);
            event = new PlayerLanguageChangeEvent(player, oldLang, language);
        }

        plugin.getServer().getPluginManager().callEvent(event);
    }

    public void reload() {
        plugin.getDatabaseManager().loadPlayerData(this);
    }
    // -----

    public Player getPlayer() {
        return player;
    }
}