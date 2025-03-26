package me.lucaaa.languagelib.api.language;

import java.util.Map;

/**
 * Contains the language of a sender.
 */
@SuppressWarnings("unused")
public interface Messageable {
    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param key The key of the message to send.
     */
    default void sendMessage(String key) {
        sendMessage(key, null, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param key The key of the message to send.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    default void sendMessage(String key, boolean addPrefix) {
        sendMessage(key, null, addPrefix);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     */
    default void sendMessage(String key, Map<String, String> placeholders) {
        sendMessage(key, placeholders, true);
    }

    /**
     * Sends the given message to the player in his language. <br>
     *
     * The message will be parsed (colors and player placeholders).
     * @param key The key of the message to send.
     * @param placeholders Parts of the text (keys of the map) that will be replaced with the values of said keys.
     * @param addPrefix Whether the prefix should be added at the beginning of the message.
     */
    void sendMessage(String key, Map<String, String> placeholders, boolean addPrefix);
}