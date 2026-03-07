package me.lucaaa.languagelib.api.events;

/**
 * Event called when the LanguageLib plugin is reloaded.
 * <p>
 * When the plugin is reloaded, all API instances are reloaded too and, therefore, messages are reloaded from the languages files.
 */
public class PluginReloadEvent extends LanguageEvent {
    /**
     * Constructor for the event.
     * <p>
     * This is for internal use only and should not be called directly.
     */
    public PluginReloadEvent() {}
}