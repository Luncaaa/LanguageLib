package me.lucaaa.languagelib.data.configs;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Language;
import me.lucaaa.languagelib.managers.ItemsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class LanguageImpl extends Config implements Language {
    private final Map<String, String> messages = new HashMap<>();
    private final Map<String, List<String>> lists = new HashMap<>();
    private final String name;
    private final String code;

    public LanguageImpl(LanguageLib plugin, JavaPlugin apiPlugin, String languagesFolderPath, String fileName) {
        super(plugin, apiPlugin, languagesFolderPath + File.separator + fileName, false);

        this.name = config.getString("name", "No name set");
        this.code = getNameWithoutExtension(file);

        // Cache head in the ItemsManager.
        if (plugin.equals(apiPlugin)) {
            String base64 = getOrDefault("flag", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZlNTIyZDkxODI1MjE0OWU2ZWRlMmVkZjNmZTBmMmMyYzU4ZmVlNmFjMTFjYjg4YzYxNzIwNzIxOGFlNDU5NSJ9fX0=");
            plugin.getManager(ItemsManager.class).cacheHead(fileName, base64);
        }

        // Each key that is not a config section is added to the map along with its corresponding message
        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;
            if (config.isString(key)) {
                messages.put(key, config.getString(key));

            } else if (config.isList(key)) {
                lists.put(key, config.getStringList(key));
            }
        }
    }

    public String getMessage(String key) {
        if (!messages.containsKey(key)) {
            plugin.log(Level.WARNING, "Key \"" + key + "\" not found for language \"" + file.getName() + "\"!");
            return "Key not found for your language! (" + getCode() + ")";
        }

        return messages.get(key);
    }

    public List<String> getList(String key) {
        if (!lists.containsKey(key)) {
            plugin.log(Level.WARNING, "Key \"" + key + "\" not found for language \"" + file.getName() + "\"!");
            ArrayList<String> notFound = new ArrayList<>();
            notFound.add("Key not found for your language! (" + getCode() + ")");
            return notFound;
        }

        return lists.get(key);
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return code;
    }
}