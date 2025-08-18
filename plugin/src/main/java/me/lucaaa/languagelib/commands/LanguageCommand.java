package me.lucaaa.languagelib.commands;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Language;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.commands.subcommands.*;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.inventory.LanguageInventory;
import me.lucaaa.languagelib.managers.messages.MessagesManagerImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageCommand implements TabExecutor {
    private final LanguageLib plugin;
    private final MessagesManagerImpl messagesManager;
    private final HashMap<String, SubCommand> subCommands = new HashMap<>();

    public LanguageCommand(LanguageLib plugin) {
        this.plugin = plugin;
        this.messagesManager = plugin.getPluginMessagesManager();

        subCommands.put("reload", new ReloadSubCommand(plugin));
        subCommands.put("help", new HelpSubCommand(plugin, subCommands));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Tab completions for each subcommand. If the user is going to type the first argument, and it does not need any permission
        // to be executed, complete it. If it needs a permission, check if the user has it and add more completions.
        if (args.length == 1) {
            for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
                if (entry.getValue().getNeededPermission() == null || sender.hasPermission(entry.getValue().getNeededPermission())) {
                    completions.add(entry.getKey());
                }

                // Add all languages.
                completions.addAll(messagesManager.getLanguagesNames().stream().map(name -> name.split("\\.")[0]).collect(Collectors.toList()));
            }
        }

        // Command's second argument.
        SubCommand subcommand = subCommands.get(args[0]);
        if (args.length >= 2 && subcommand != null && sender.hasPermission(subcommand.getNeededPermission())) {
            completions = subCommands.get(args[0]).getTabCompletions(sender, args);
        }

        // Filters the array so only the completions that start with what the user is typing are shown.
        // For example, it can complete "reload", "removeDisplay" and "help". If the user doesn't type anything, all those
        // options will appear. If the user starts typing "r", only "reload" and "removeDisplay" will appear.
        // args[args.size-1] -> To get the argument the user is typing (first, second...)
        return completions.stream().filter(completion -> completion.toLowerCase().contains(args[args.length-1].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Messageable messageable = messagesManager.getMessageable(sender);

        // If there are no arguments, show an error.
        if (args.length == 0) {
            if (sender instanceof Player) {
                plugin.getInventoriesManager().handleOpen((Player) sender, new LanguageInventory(plugin, messageable));
            } else {
                messageable.sendMessage("commands.main.player_command_only");
            }
            return true;
        }

        // If the subcommand does not exist, check if it's a language.
        if (!subCommands.containsKey(args[0])) {
            PlayerData playerData = plugin.getPlayersManager().get((Player) sender);
            Language language = messagesManager.get(args[0] + ".yml");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%language%", args[0]);

            if (language == null) {
                messageable.sendMessage("commands.language.not_exist", placeholders);
            } else {
                if (language.equals(playerData.getLang())) {
                    messageable.sendMessage("commands.language.already_selected", placeholders);
                } else {
                    playerData.setLang(language, false);
                    messageable.sendMessage("commands.language.success", placeholders);
                }
            }
            return true;
        }

        // If the subcommand exists, get it from the map.
        SubCommand subCommand = subCommands.get(args[0]);
        Map<String, String> placeholders = subCommand.getPlaceholders(sender);

        // If the player who ran the command does not have the needed permissions, show an error.
        if (subCommand.getNeededPermission() != null && !sender.hasPermission(subCommand.getNeededPermission())) {
            messagesManager.sendMessage(sender, "commands.main.no_permission", placeholders);
            return true;
        }

        // If the command was executed by console but only players can execute it, show an error.
        if (!(sender instanceof Player) && !subCommand.isExecutableByConsole()) {
            messagesManager.sendMessage(sender, "commands.main.player_command_only", placeholders);
            return true;
        }

        // If the user entered fewer arguments than the subcommand needs, an error will appear.
        // args.size - 1 because the name of the subcommand is not included in the minArguments
        if (args.length - 1 < subCommand.getMinArguments()) {
            messagesManager.sendMessage(sender, "commands.not_enough_arguments", placeholders);
            messagesManager.sendMessage(sender, "commands.command_usage", placeholders);
            return true;
        }

        // If the command is valid, run it.
        subCommand.run(sender, args);
        return true;
    }
}