package me.lucaaa.languagelib.api.language;

/**
 * Represents every language file.
 */
public interface Language {
    /**
     * Gets the language's name (set in LanguageLib's language file).
     * @return The language's name.
     */
    String getName();

    /**
     * The name of the file associated to this language (for example, en_us.yml).
     * <p>
     * This object will be retrievable by the API even if your plugin doesn't have such language (through the events, for example).
     * @return The name of the file associated to the language.
     */
    String getFileName();

    /**
     * Gets the language's code (file name without extension).
     * @return The language's code.
     */
    String getCode();
}