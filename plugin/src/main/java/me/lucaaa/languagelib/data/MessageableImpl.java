package me.lucaaa.languagelib.data;

import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.data.configs.Language;
import org.bukkit.command.CommandSender;

public interface MessageableImpl extends Messageable {
    void sendMessage(String message, boolean addPrefix);

    CommandSender getSender();

    Language getLang();

    boolean isPlayer();
}