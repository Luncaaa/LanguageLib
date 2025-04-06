package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.events.AsyncPlayerLanguageLoadEventImpl;
import me.lucaaa.languagelib.api.events.LanguageEvent;
import me.lucaaa.languagelib.api.events.PlayerLanguageChangeEventImpl;
import me.lucaaa.languagelib.api.language.Language;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import org.bukkit.entity.Player;

public class PlayerData implements LangProvider {
    private final LanguageLib plugin;
    private final Player player;
    private LanguageImpl lang;

    public PlayerData(LanguageLib plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        plugin.getDatabaseManager().loadPlayerData(this);
    }

    // ---[ Messages ]---
    @Override
    public LanguageImpl getLang() {
        return lang;
    }

    /**
     * Sets the player's language.
     * @param language The language to set.
     * @param onJoin Whether this method was called when the player joined or the player set his language manually.
     */
    public void setLang(LanguageImpl language, boolean onJoin) {
        Language oldLang = lang;
        this.lang = language;

        LanguageEvent event;
        if (onJoin) {
            event = new AsyncPlayerLanguageLoadEventImpl(player, language);
        } else {
            plugin.getDatabaseManager().savePlayerData(this);
            event = new PlayerLanguageChangeEventImpl(player, oldLang, language);
        }

        plugin.getServer().getPluginManager().callEvent(event);
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