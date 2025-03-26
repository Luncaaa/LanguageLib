package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.data.configs.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class MessageableImpl implements Messageable {
    private final CommandSender sender;
    private final boolean isPlayer;
    private final MessagesManager messagesManager;

    public MessageableImpl(CommandSender sender, MessagesManager messagesManager) {
        this.sender = sender;
        this.isPlayer = sender instanceof Player;
        this.messagesManager = messagesManager;
    }

    @Override
    public void sendMessage(String key, Map<String, String> placeholders, boolean addPrefix) {
        messagesManager.sendMessage(this, key, placeholders, addPrefix);
    }

    public CommandSender getSender() {
        return sender;
    }

    public abstract Language getLang();

    public boolean isPlayer() {
        return isPlayer;
    }

    public MessageableImpl withManager(MessagesManager messagesManager) {
        return new MessageableImpl(sender, messagesManager) {
            @Override
            public Language getLang() {
                return MessageableImpl.this.getLang();
            }
        };
    }
}