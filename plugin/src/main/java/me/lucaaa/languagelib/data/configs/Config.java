package me.lucaaa.languagelib.data.configs;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class Config {
    protected final LanguageLib plugin;
    protected final File file;
    protected final YamlConfiguration config;

    public Config(LanguageLib plugin, String path, boolean createIfNotExists) {
        this(plugin, plugin.getDataFolder().getAbsolutePath(), path, createIfNotExists);
    }

    public Config(LanguageLib plugin, String dataFolderPath, String path, boolean createIfNotExists) {
        this.plugin = plugin;
        this.file = new File(dataFolderPath + File.separator + path);

        if (!file.exists() && createIfNotExists) {
            plugin.saveResource(path, false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveConfig(LanguageLib plugin, String filePath) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + filePath);

        if (!file.exists()) {
            plugin.saveResource(filePath, false);
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected ConfigurationSection getSection(String name) {
        ConfigurationSection section = config.getConfigurationSection(name);
        if (section == null) {
            section = config.createSection(name);
            plugin.log(Level.WARNING, "Missing section \"" + name + "\" in \"" + file.getName() + "\" file! Created an empty section.");
        }

        return section;
    }

    protected <T> T getOrDefault(String setting, T def) {
        if (!config.contains(setting)) {
            plugin.log(Level.WARNING, "Missing setting \"" + setting + "\" in \"" + file.getName() + "\" file! Setting to default value: " + def);
            config.set(setting, def);
            return def;
        }

        Object data = config.get(setting);
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) def.getClass();

        if (!clazz.isInstance(data)) {
            plugin.log(Level.WARNING, "Setting \"" + setting + "\" is not a \"" + clazz.getSimpleName() + "\" value in \"" + file.getName() + "\" file! Setting to default value: " + def);
            // Config value won't be set in case the user just forgot the quotes (so he doesn't lose data).
            // config.set(setting, def);
            return def;
        }

        return clazz.cast(data);
    }

    public static String getNameWithoutExtension(File file) {
        return getNameWithoutExtension(file.getName());
    }

    public static String getNameWithoutExtension(String name) {
        String[] parts = name.split("\\.");
        // Remove extension. "-1" because end index is exclusive.
        return String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1));
    }
}