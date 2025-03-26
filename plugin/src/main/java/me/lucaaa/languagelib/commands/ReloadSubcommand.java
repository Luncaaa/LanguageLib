package me.lucaaa.languagelib.commands;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand extends Subcommand {
    public ReloadSubcommand(LanguageLib plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String description() {
        return "Reloads the plugin's configuration files.";
    }

    @Override
    public String usage() {
        return "/aua reload";
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
        return "aua.reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.reloadConfigs(messagesManager.getMessageable(sender));
    }
}