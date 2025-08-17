package me.lucaaa.languagelib.managers.messages;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.configs.Config;
import me.lucaaa.languagelib.utils.ProvidedConfig;

import java.io.File;

public class ServerMessagesManager extends MessagesManagerImpl {
    public ServerMessagesManager(LanguageLib plugin) {
        super(plugin, plugin, false, plugin.getMainConfig().prefix, "langs" + File.separator + "server");
    }

    @Override
    public void reload() {
        // Save default languages
        for (ProvidedConfig lang : ProvidedConfig.values()) {
            if (plugin.getMainConfig().ignoredLanguages.contains(Config.getNameWithoutExtension(lang.getFileName()))) {
                continue;
            }
            Config.saveConfig(plugin, "langs" + File.separator + "server" + File.separator + lang.getFileName());
        }

        super.reload();
    }
}