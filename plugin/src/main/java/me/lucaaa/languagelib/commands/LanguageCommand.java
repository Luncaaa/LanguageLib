package me.lucaaa.languagelib.commands;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.inventory.LanguageInventory;
import me.lucaaa.languagelib.managers.InventoriesManager;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import me.lucaaa.languagelib.managers.PlayersManager;
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

    public LanguageCommand(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = plugin.getManager(MessagesManagerImpl.class).getLanguagesNames().stream().map(element -> element.split("\\.")[0]).collect(Collectors.toList());
            if (sender.hasPermission("lang.reload")) arguments.add("reload");
            return arguments;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessagesManagerImpl messagesManager = plugin.getManager(MessagesManagerImpl.class);

        if (!(sender instanceof Player)) {
            Messageable messageable = messagesManager.getServerConsole();
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfigs(sender);
            } else {
                messageable.sendMessage("commands.main.player_command_only", null);
            }
            return true;
        }

        Player player = (Player) sender;
        Messageable messageable = messagesManager.getMessageable(sender);
        PlayerData playerData = plugin.getManager(PlayersManager.class).get(player);

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("lang.reload")) {
                    plugin.reloadConfigs(sender);
                } else {
                    messageable.sendMessage("commands.main.no_permission", null);
                }
                return true;
            }

            Language language = messagesManager.get(args[0] + ".yml");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%language%", args[0]);
            if (language == null) {
                messageable.sendMessage("commands.language.not_exist", placeholders);
            } else {
                if (language.equals(playerData.getLang())) {
                    messageable.sendMessage("commands.language.already_selected", placeholders);
                } else {
                    playerData.setLang(language);
                    messageable.sendMessage("commands.language.success", placeholders);
                }
            }

        } else {
            plugin.getManager(InventoriesManager.class).handleOpen(player, new LanguageInventory(plugin, messageable));
        }

        return true;
    }
}