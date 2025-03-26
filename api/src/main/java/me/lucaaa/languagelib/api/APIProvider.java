package me.lucaaa.languagelib.api;

import org.bukkit.plugin.Plugin;

/**
 * INTERNAL USE ONLY - DO NOT USE!
 * @hidden
 */
public abstract class APIProvider {

    private static APIProvider implementation;

    public static APIProvider getImplementation() {
        if (APIProvider.implementation == null) {
            throw new IllegalStateException("The LanguageLib API implementation is not set yet.");
        }
        return APIProvider.implementation;
    }

    public static void setImplementation(APIProvider implementation) {
        if (APIProvider.implementation != null) {
            throw new IllegalStateException("The LanguageLib API implementation is already set.");
        }
        APIProvider.implementation = implementation;
    }

    public abstract LanguageAPI getAPI(Plugin plugin, String prefix, String languagesFolderPath);
}