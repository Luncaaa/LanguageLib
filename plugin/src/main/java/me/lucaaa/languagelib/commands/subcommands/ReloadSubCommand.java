package me.lucaaa.languagelib.commands.subcommands;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {
    public ReloadSubCommand(LanguageLib plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.reloadConfigs(messagesManager.getMessageable(sender));
    }
}