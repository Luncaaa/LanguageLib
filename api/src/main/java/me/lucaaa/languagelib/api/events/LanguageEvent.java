package me.lucaaa.languagelib.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for LanguageLib's events.
 */
@SuppressWarnings("unused")
public class LanguageEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    /**
     * Constructor for non-async events.
     * <p>
     * This is for internal use only and should not be called directly.
     */
    public LanguageEvent() {
        this(false);
    }

    /**
     Constructor for async and non-async events.
     * <p>
     * This is for internal use only and should not be called directly.
     * @param isAsync Whether the event should be run async or not.
     */
    public LanguageEvent(boolean isAsync) {
        super(isAsync);
    }

    /**
     * Required by Spigot's {@link Event}
     * @return The HandlerList.
     */
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}