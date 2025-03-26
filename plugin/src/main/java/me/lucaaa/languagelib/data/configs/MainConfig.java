package me.lucaaa.languagelib.data.configs;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.Database;

public class MainConfig extends Config {
    private final LanguageLib plugin;

    public final String prefix;
    public final String defaultLang;
    public final boolean usePlayerLocale;
    public final Database database;

    public MainConfig(LanguageLib plugin) {
        super(plugin, "config.yml", true);
        this.plugin = plugin;

        this.prefix = getOrDefault("prefix", "&cAmong&9Us&7&lMC");
        this.defaultLang = getOrDefault("default_lang", "en_us.yml");
        this.usePlayerLocale = getOrDefault("use_player_locale", true);
        this.database = new Database(
                getOrDefault("database.use_mysql", true),
                getOrDefault("database.mysql.name", "au_players_data"),
                getOrDefault("database.mysql.host", "localhost"),
                getOrDefault("database.mysql.port", "3306"),
                getOrDefault("database.mysql.username", "root"),
                getOrDefault("database.mysql.password", "")
        );
    }
}