package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class APIProviderImplementation extends APIProvider {
    private final LanguageLib plugin;
    private final Map<Plugin, APIImplementation> apiMap = new ConcurrentHashMap<>();

    public APIProviderImplementation(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public APIImplementation getAPI(Plugin plugin, String prefix, String languagesFolderPath) {
        return apiMap.computeIfAbsent(plugin, p -> new APIImplementation(this.plugin, plugin, prefix, languagesFolderPath));
    }

    public Map<Plugin, APIImplementation> getApiMap() {
        return apiMap;
    }
}