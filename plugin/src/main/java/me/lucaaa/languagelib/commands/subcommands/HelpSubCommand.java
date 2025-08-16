package me.lucaaa.languagelib.commands.subcommands;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpSubCommand extends SubCommand {
    private final Map<String, SubCommand> subCommands;

    public HelpSubCommand(LanguageLib plugin, Map<String, SubCommand> subCommands) {
        super(plugin);
        this.subCommands = subCommands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Messageable messageable = messagesManager.getMessageable(sender);

        messageable.sendColored("&c---------[ /language - /lang ]---------");
        messageable.sendMessage("commands.help.introduction", null, false);

        for (SubCommand value : subCommands.values()) {
            if (value.getNeededPermission() == null || sender.hasPermission(value.getNeededPermission()) || sender.hasPermission("lang.admin")) {
                messageable.sendColored(" &7- &6" + value.getUsage(sender) + "&7: &e" + value.getDescription(sender));
            }
        }
    }
}