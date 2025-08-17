package me.lucaaa.languagelib.managers.messages;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Config;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.lucaaa.languagelib.utils.NoLanguagesFoundException;
import me.lucaaa.languagelib.utils.ProvidedConfig;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;

public class PluginMessagesManager extends MessagesManagerImpl {
    private LanguageImpl defaultLang;

    public PluginMessagesManager(LanguageLib plugin) {
        super(plugin, plugin, true, plugin.getMainConfig().prefix, "langs" + File.separator + "plugin");
    }

    public LanguageImpl getDefaultLang() {
        return defaultLang;
    }

    @Override
    public void reload() {
        // Save default languages
        for (ProvidedConfig lang : ProvidedConfig.values()) {
            if (plugin.getMainConfig().ignoredLanguages.contains(Config.getNameWithoutExtension(lang.getFileName()))) {
                continue;
            }
            Config.saveConfig(plugin, "langs" + File.separator + "plugin" + File.separator + lang.getFileName());
        }

        // Load all files
        super.reload();

        // Sets the default language by fetching it from the config file.
        LanguageImpl defLang;
        String defLangName = plugin.getMainConfig().defaultLang;

        defLang = get(defLangName, false);

        // If the default language is not found, use the first file found.
        if (defLang == null) {
            Optional<LanguageImpl> first = values.values().stream().findFirst();
            if (first.isPresent()) {
                defLang = first.get();
                plugin.log(Level.WARNING, "Default language file \"" + defLangName + "\" was not found. Switching to \"" + defLang + "\".");
            } else {
                throw new NoLanguagesFoundException("No valid languages in " + fullLanguageFolderPath);
            }
        }

        this.defaultLang = defLang;
    }
}