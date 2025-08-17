package me.lucaaa.languagelib.api.language;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

/**
 * Contains the language files and their messages.
 */
@SuppressWarnings("unused")
public interface MessagesManager {
    /**
     * Reloads the API instance.
     * <p>
     * Use it when your plugin's reload command is used so that changes in language files are reflected.
     */
    void reload();

    /**
     * Sends the given message to the sender with colors.
     * <p>
     * Legacy color codes (&amp;) and minimessage will be parsed.
     * @param sender To whom the message should be sent.
     * @param message The message to send.
     */
    default void sendColored(CommandSender sender, String message) {
        sendColored(getMessageable(sender), message);
    }

    /**
     * Sends the given message to the sender with colors.
     * <p>
     * Legacy color codes (&amp;) and minimessage will be parsed.
     * @param messageable To whom the message should be sent.
     * @param message The message to send.
     */
    void sendColored(Messageable messageable, String message);

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param sender To whom the message should be sent.
     * @param key The key of the message to send.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(CommandSender sender, String key, boolean addPrefix) {
        sendMessage(getMessageable(sender), key, null, addPrefix);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(Messageable messageable, String key, boolean addPrefix) {
        sendMessage(messageable, key, null, addPrefix);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param sender To whom the message should be sent.
     * @param key The key of the message to send.
     */
    default void sendMessage(CommandSender sender, String key) {
        sendMessage(getMessageable(sender), key, null, true);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     */
    default void sendMessage(Messageable messageable, String key) {
        sendMessage(messageable, key, null, true);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param sender To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     */
    default void sendMessage(CommandSender sender, String key, Map<String, String> placeholders) {
        sendMessage(getMessageable(sender), key, placeholders, true);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     */
    default void sendMessage(Messageable messageable, String key, Map<String, String> placeholders) {
        sendMessage(messageable, key, placeholders, true);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param sender To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(CommandSender sender, String key, Map<String, String> placeholders, boolean addPrefix) {
        sendMessage(getMessageable(sender), key, placeholders, addPrefix);
    }

    /**
     * Sends the given message to the sender in his language.
     * <p>
     * The message will be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    void sendMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix);

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will be parsed (colors and player placeholders)
     * @param sender For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getList(CommandSender sender, String key) {
        return getList(getMessageable(sender), key, null);
    }

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getList(Messageable messageable, String key) {
        return getList(messageable, key, null);
    }

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will be parsed (colors and player placeholders)
     * @param sender For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The list from the language config.
     */
    default List<String> getList(CommandSender sender, String key, Map<String, String> placeholders) {
        return getList(getMessageable(sender), key, placeholders);
    }

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The list from the language config.
     */
    List<String> getList(Messageable messageable, String key, Map<String, String> placeholders);

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will NOT be parsed (colors and player placeholders)
     * @param sender For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    default List<String> getUnparsedList(CommandSender sender, String key) {
        return getUnparsedList(getMessageable(sender), key);
    }

    /**
     * Gets a list of messages from the language config.
     * <p>
     * The messages will NOT be parsed (colors and player placeholders)
     * @param messageable For whom the list is being obtained (his language).
     * @param key The key of the list to get.
     * @return The list from the language config.
     */
    List<String> getUnparsedList(Messageable messageable, String key);

    /**
     * Sends the given message to the player in his language.
     * <p>
     * The message will NOT be parsed (colors and player placeholders).
     * @param sender To whom the message should be sent.
     * @param key The key of the message to send.
     * @return The unparsed message in the player's language.
     */
    default String getUnparsedMessage(CommandSender sender, String key) {
        return getUnparsedMessage(getMessageable(sender), key);
    }

    /**
     * Gets the given message in the sender's language.
     * <p>
     * The message will NOT be parsed (colors and player placeholders).
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to send.
     * @return The unparsed message in the sender's language.
     */
    String getUnparsedMessage(Messageable messageable, String key);

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @return The message in the sender's language as String with minimessage format.
     */
    default String getMessageMinimessage(CommandSender sender, String key) {
        return getMessageLegacy(getMessageable(sender), key, null, true);
    }

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @return The message in the sender's language as String with minimessage format.
     */
    default String getMessageMinimessage(Messageable messageable, String key) {
        return getMessageMinimessage(messageable, key, null, true);
    }

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The message in the sender's language as String with minimessage format.
     */
    default String getMessageMinimessage(CommandSender sender, String key, Map<String, String> placeholders) {
        return getMessageMinimessage(getMessageable(sender), key, placeholders, true);
    }

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The message in the sender's language as String with minimessage format.
     */
    default String getMessageMinimessage(Messageable messageable, String key, Map<String, String> placeholders) {
        return getMessageMinimessage(messageable, key, placeholders, true);
    }

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     * @return The message in the sender's language as String with minimessage format.
     */
    default String getMessageMinimessage(CommandSender sender, String key, Map<String, String> placeholders, boolean addPrefix) {
        return getMessageMinimessage(getMessageable(sender), key, placeholders, addPrefix);
    }

    /**
     * Gets the given message in the sender's language as a String with minimessage format.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     * @return The message in the sender's language as String with minimessage format.
     */
    String getMessageMinimessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix);

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @return The message in the sender's language as a legacy String.
     */
    default String getMessageLegacy(CommandSender sender, String key) {
        return getMessageLegacy(getMessageable(sender), key, null, true);
    }

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @return The message in the sender's language as a legacy String.
     */
    default String getMessageLegacy(Messageable messageable, String key) {
        return getMessageLegacy(messageable, key, null, true);
    }

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The message in the sender's language as a legacy String.
     */
    default String getMessageLegacy(CommandSender sender, String key, Map<String, String> placeholders) {
        return getMessageLegacy(getMessageable(sender), key, placeholders, true);
    }

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @return The message in the sender's language as a legacy String.
     */
    default String getMessageLegacy(Messageable messageable, String key, Map<String, String> placeholders) {
        return getMessageLegacy(messageable, key, placeholders, true);
    }

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param sender To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     * @return The message in the sender's language as a legacy String.
     */
    default String getMessageLegacy(CommandSender sender, String key, Map<String, String> placeholders, boolean addPrefix) {
        return getMessageLegacy(getMessageable(sender), key, placeholders, addPrefix);
    }

    /**
     * Gets the given message in the sender's language as a legacy String.
     * <p>
     * It is recommended to use {@link MessagesManager#sendMessage(CommandSender, String)} instead.
     * The messages will be parsed (colors and player placeholders)
     * @param messageable To whom the message should be sent.
     * @param key The key of the message to get.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     * @return The message in the sender's language as a legacy String.
     */
    String getMessageLegacy(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix);

    /**
     * Gets the language data for the given CommandSender.
     * @param sender The sender (can be console).
     * @return The language data.
     */
    Messageable getMessageable(CommandSender sender);

    /**
     * Gets the server's console as a messageable.
     * <p>
     * Messages will be sent in the default language set in LanguageLib's config.yml file.
     * @return The server console as a messageable.
     */
    Messageable getServerConsole();
}