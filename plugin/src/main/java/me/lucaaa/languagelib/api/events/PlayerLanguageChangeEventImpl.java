package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;

public class PlayerLanguageChangeEventImpl extends PlayerLanguageChangeEvent {
    private final Player player;
    private final Language oldLanguage;
    private final Language newLanguage;

    public PlayerLanguageChangeEventImpl(Player player, Language oldLanguage, Language newLanguage) {
        this.player = player;
        this.oldLanguage = oldLanguage;
        this.newLanguage = newLanguage;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Language getOldLanguage() {
        return oldLanguage;
    }

    @Override
    public Language getNewLanguage() {
        return newLanguage;
    }
}