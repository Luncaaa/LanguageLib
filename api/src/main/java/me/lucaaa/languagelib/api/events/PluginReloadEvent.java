package me.lucaaa.languagelib.api.events;

/**
 * Event called when the LanguageLib plugin is reloaded.
 * <p>
 * When the plugin is reloaded, all API instances are reloaded too and, therefore, messages are reloaded from the languages files.
 */
@SuppressWarnings("unused")
public class PluginReloadEvent extends LanguageEvent {}