package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Event called when a player joins and his language is retrieved from the database.
 * <p>
 * This event is called asynchronously because all calls to the database are done async.
 * Because of that, this event might be called a bit after the {@link PlayerJoinEvent} event is called.
 */
@SuppressWarnings("unused")
public abstract class AsyncPlayerLanguageLoadEvent extends LanguageEvent {
    /**
     * Constructor for the event.
     * <p>
     * This is for internal use only and should not be called directly.
     */
    public AsyncPlayerLanguageLoadEvent() {
        super(true);
    }

    /**
     * Returns the player whose language was set.
     * @return The player whose language was set.
     */
    public abstract Player getPlayer();

    /**
     * Returns the language that was set.
     * @return The language that was set.
     */
    public abstract Language getLanguage();
}