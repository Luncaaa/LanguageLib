package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;

/**
 * Event called when a player changes his language through the command or the inventory.
 */
@SuppressWarnings("unused")
public abstract class PlayerLanguageChangeEvent extends LanguageEvent {
    /**
     * Returns the player whose language was changed.
     * @return The player whose language was changed.
     */
    public abstract Player getPlayer();

    /**
     * Returns the player's old language.
     * @return The player's old language.
     */
    public abstract Language getOldLanguage();

    /**
     * Returns the player's new language.
     * @return The player's new language.
     */
    public abstract Language getNewLanguage();
}