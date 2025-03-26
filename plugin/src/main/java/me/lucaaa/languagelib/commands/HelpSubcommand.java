package me.lucaaa.languagelib.commands;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpSubcommand extends Subcommand {
    private final Map<String, Subcommand> subCommands;

    public HelpSubcommand(LanguageLib plugin, Map<String, Subcommand> subCommands) {
        super(plugin);
        this.subCommands = subCommands;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Information about the commands the plugin has.";
    }

    @Override
    public String usage() {
        return "/language help";
    }

    @Override
    public int minArguments() {
        return 0;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String neededPermission() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        messagesManager.sendMessage(sender, "&c---------[Language subcommands ]---------", false);

        messagesManager.sendMessage(sender, "&cCommands: &7&o([] - mandatory args, <> - optional args)", false);
        for (Subcommand subcommand : subCommands.values()) {
            if (subcommand.neededPermission() == null || sender.hasPermission(subcommand.neededPermission())) {
                messagesManager.sendMessage(sender, " &7- &6" + subcommand.usage() + "&7: &e" + subcommand.description(), false);
            }
        }
    }
}