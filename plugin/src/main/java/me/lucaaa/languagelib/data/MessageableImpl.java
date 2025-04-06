package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.HashMap;

public class MessageableImpl implements Messageable {
    private final CommandSender sender;
    private final boolean isPlayer;
    private final MessagesManager messagesManager;
    private final LangProvider langProvider;

    public MessageableImpl(CommandSender sender, MessagesManager messagesManager, LangProvider langProvider) {
        this.sender = sender;
        this.isPlayer = sender instanceof Player;
        this.messagesManager = messagesManager;
        this.langProvider = langProvider;
    }

    @Override
    public void sendMessage(String key, Map<String, String> placeholders, boolean addPrefix) {
        messagesManager.sendMessage(this, key, placeholders, addPrefix);
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public LanguageImpl getLanguage() {
        return langProvider.getLang();
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%language%", getLanguage().getName());
        placeholders.put("%language_code%", getLanguage().getCode());
        return placeholders;
    }
}