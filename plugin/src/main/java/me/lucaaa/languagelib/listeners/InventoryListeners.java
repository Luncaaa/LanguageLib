package me.lucaaa.languagelib.listeners;

import me.lucaaa.languagelib.LanguageLib;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListeners implements Listener {
    private final LanguageLib plugin;

    public InventoryListeners(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getInventoriesManager().handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        plugin.getInventoriesManager().handleClose((Player) event.getPlayer());
    }
}