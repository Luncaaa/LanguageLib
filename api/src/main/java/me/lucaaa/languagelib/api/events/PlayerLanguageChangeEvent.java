package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Event called when a player changes his language through the command or the inventory.
 */
@SuppressWarnings("unused")
public class PlayerLanguageChangeEvent extends LanguageEvent {
    private final Player player;
    private final Language oldLanguage;
    private final Language newLanguage;

    /**
     * Constructor for the event.
     * <p>
     * This is for internal use only and should not be called directly.
     * @param player The player whose language changed.
     * @param oldLanguage The old language.
     * @param newLanguage The new language.
     */
    @ApiStatus.Internal
    public PlayerLanguageChangeEvent(Player player, Language oldLanguage, Language newLanguage) {
        this.player = player;
        this.oldLanguage = oldLanguage;
        this.newLanguage = newLanguage;
    }

    /**
     * Returns the player whose language was changed.
     * @return The player whose language was changed.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the player's old language.
     * @return The player's old language.
     */
    public Language getOldLanguage() {
        return oldLanguage;
    }

    /**
     * Returns the player's new language.
     * @return The player's new language.
     */
    public Language getNewLanguage() {
        return newLanguage;
    }
}