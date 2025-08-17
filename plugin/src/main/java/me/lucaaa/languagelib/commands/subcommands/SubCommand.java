package me.lucaaa.languagelib.commands.subcommands;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.managers.messages.MessagesManagerImpl;
import me.lucaaa.languagelib.managers.messages.PluginMessagesManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class SubCommand {
    protected final LanguageLib plugin;
    protected final MessagesManagerImpl messagesManager;

    public SubCommand(LanguageLib plugin) {
        this.plugin = plugin;
        this.messagesManager = plugin.getManager(PluginMessagesManager.class);
    }

    public abstract String getName();

    public String getDescription(CommandSender sender) {
        return messagesManager.getUnparsedMessage(sender, "commands." + getName() + ".description");
    }

    public String getUsage(CommandSender sender) {
        return "/lang " + getName();
    }

    public int getMinArguments() {
        return 0;
    }

    public boolean isExecutableByConsole() {
        return true;
    }

    public String getNeededPermission() {
        return null;
    }

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public abstract void run(CommandSender sender, String[] args);

    public Map<String, String> getPlaceholders(CommandSender sender) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%subcommand%", getName());
        placeholders.put("%description%", getDescription(sender));
        placeholders.put("%usage%", getUsage(sender));
        placeholders.put("%minArguments%", String.valueOf(getMinArguments()));
        placeholders.put("%permission%", getNeededPermission());
        return placeholders;
    }
}