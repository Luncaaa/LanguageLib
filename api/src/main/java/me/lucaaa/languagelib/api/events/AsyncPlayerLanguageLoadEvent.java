package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Event called when a player joins and his language is retrieved from the database.
 * <p>
 * This event is called asynchronously because all calls to the database are done async.
 * Because of that, this event might be called a bit after the {@link PlayerJoinEvent} event is called.
 */
public class AsyncPlayerLanguageLoadEvent extends LanguageEvent {
    private final Player player;
    private final Language language;

    /**
     * Constructor for the event.
     * <p>
     * This is for internal use only and should not be called directly.
     * @param player The player whose language was set.
     * @param language The language.
     */
    @ApiStatus.Internal
    public AsyncPlayerLanguageLoadEvent(Player player, Language language) {
        super(true);
        this.player = player;
        this.language = language;
    }

    /**
     * Returns the player whose language was set.
     * @return The player whose language was set.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the language that was set.
     * @return The language that was set.
     */
    public Language getLanguage() {
        return language;
    }
}