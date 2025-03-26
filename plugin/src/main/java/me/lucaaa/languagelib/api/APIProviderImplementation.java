package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class APIProviderImplementation extends APIProvider {
    private final LanguageLib languageLib;
    private final Map<Plugin, APIImplementation> apiMap = new ConcurrentHashMap<>();

    public APIProviderImplementation(LanguageLib plugin) {
        this.languageLib = plugin;
    }

    @Override
    public APIImplementation getAPI(Plugin plugin, String prefix, String languagesFolderPath) {
        return apiMap.computeIfAbsent(plugin, p -> new APIImplementation(languageLib, plugin, prefix, languagesFolderPath));
    }

    public void reload() {
        for (APIImplementation api : apiMap.values()) {
            api.reload();
        }
    }

    public void onLeave(Player player) {
        for (APIImplementation api : apiMap.values()) {
            api.onLeave(player);
        }
    }
}