package me.lucaaa.languagelib.api;

import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * LanguageLib' API class.
*/
@SuppressWarnings("unused")
public interface LanguageAPI {
    /**
     * Gets an instance of the LanguagesLib plugin API.
     * @param plugin An instance of your plugin.
     * @param prefix The text to put in front of texts if "addPrefix" is true (can be blank).
     * @param languagesFolderPath The path to the folder with your languages files (inside your plugin's configuration folder).
     * @return The instance of the API.
     */
    static LanguageAPI getInstance(Plugin plugin, String prefix, String languagesFolderPath) {
        return APIProvider.getImplementation().getAPI(plugin, prefix, languagesFolderPath);
    }

    /**
     * Gets the messages manager.<br>
     *
     * This contains the list of languages and their messages.
     * @return The messages manager.
     */
    MessagesManager getMessagesManager();

    /**
     * Gets the messageable data for a sender (player or console).<br>
     *
     * This has the language necessary for getting messages.
     * @param sender The sender.
     * @return The messageable.
     */
    Messageable getMessageable(CommandSender sender);
}
