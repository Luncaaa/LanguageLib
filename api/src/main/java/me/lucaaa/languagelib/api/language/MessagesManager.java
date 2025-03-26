package me.lucaaa.languagelib.api.language;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Contains the language files and their messages.
 */
@SuppressWarnings("unused")
public interface MessagesManager {
    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param player To whom the message should be sent.
     * @param key The key of the message to send.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(Player player, String key, boolean addPrefix) {
        sendMessage(getMessageable(player), key, null, addPrefix);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(Messageable messageable, String key, boolean addPrefix) {
        sendMessage(messageable, key, null, addPrefix);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param player To whom the message should be sent.
     * @param key The key of the message to send.
     */
    default void sendMessage(Player player, String key) {
        sendMessage(getMessageable(player), key, null, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     */
    default void sendMessage(Messageable messageable, String key) {
        sendMessage(messageable, key, null, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param player To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     */
    default void sendMessage(Player player, String key, Map<String, String> placeholders) {
        sendMessage(getMessageable(player), key, placeholders, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     */
    default void sendMessage(Messageable messageable, String key, Map<String, String> placeholders) {
        sendMessage(messageable, key, placeholders, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param player To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(Player player, String key, Map<String, String> placeholders, boolean addPrefix) {
        sendMessage(getMessageable(player), key, placeholders, addPrefix);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    void sendMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix);

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will be parsed (colors and player placeholders)
     * @param player For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getList(Player player, String key) {
        return getList(getMessageable(player), key, null);
    }

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getList(Messageable messageable, String key) {
        return getList(messageable, key, null);
    }

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will be parsed (colors and player placeholders)
     * @param player For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The list from the language config.
     */
    default List<String> getList(Player player, String key, Map<String, String> placeholders) {
        return getList(getMessageable(player), key, placeholders);
    }

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The list from the language config.
     */
    List<String> getList(Messageable messageable, String key, Map<String, String> placeholders);

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will NOT be parsed (colors and player placeholders)
     * @param player For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getUnparsedList(Player player, String key) {
        return getUnparsedList(getMessageable(player), key);
    }

    /**
     * Gets a list of messages from the language config.<br>
     *
     * The messages will NOT be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    List<String> getUnparsedList(Messageable messageable, String key);

    /**
     * Sends the given message to the player in his language.<br>
     *
     * The message will NOT be parsed (colors and player placeholders).
     * @param player To whom the message should be sent.
     * @param key The key of the message to send.
     * @return The unparsed message in the player's language.
     */
    default String getUnparsedMessage(Player player, String key) {
        return getUnparsedMessage(getMessageable(player), key);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will NOT be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @return The unparsed message in the player's language.
     */
    String getUnparsedMessage(Messageable messageable, String key);

    /**
     * Gets the given message in the player's language as a legacy String.
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The unparsed message in the player's language.
     */
    default String toLegacy(Messageable messageable, String key, Map<String, String> placeholders) {
        return toLegacy(messageable, key, placeholders, true);
    }

    /**
     * Gets the given message in the player's language as a legacy String.
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     * @return The unparsed message in the player's language.
     */
    String toLegacy(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix);

    /**
     * Gets the language data for the given CommandSender.
     * @param sender The sender (can be console).
     * @return The language data.
     */
    Messageable getMessageable(CommandSender sender);

    /**
     * Gets the server's console as a messageable.<br>
     *
     * Messages will be sent in the default language set in LanguageLib's config.yml file.
     * @return The server console as a messageable.
     */
    Messageable getServerConsole();
}