package me.lucaaa.languagelib.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.managers.messages.ServerMessagesManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdersManager extends PlaceholderExpansion {
    private final LanguageLib plugin;

    public PlaceholdersManager(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lang";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Lucaaa";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    //

    /**
     * Overriding this and not onRequest because player must be online to see the text
     * @param player The player for whom the placeholder must be parsed.
     * @param params Options (legacy, minimessage, unparsed) followed by the message key (from langs/server)
     * @return The message depending on the player's language.
     */
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] parts = params.split("_");

        if (parts.length < 2) {
            return "Invalid format! Use: %lang_<legacy/minimessage/unparsed>_<path.to.key>%";
        }

        String format = parts[0];
        String key = parts[1];

        ServerMessagesManager messagesManager = plugin.getServerMessagesManager();
        if (format.equalsIgnoreCase("legacy")) {
            return messagesManager.getMessageLegacy(player, key, null, false);

        } else if (format.equalsIgnoreCase("minimessage")) {
            return messagesManager.getMessageMinimessage(player, key, null, false);

        } else if (format.equalsIgnoreCase("unparsed")) {
            return messagesManager.getUnparsedMessage(player, key);

        } else {
            return "Invalid format! Use: %lang_<legacy/minimessage/unparsed>_<path.to.key>%";
        }
    }
}