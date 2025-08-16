package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.inventory.LanguageInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoriesManager extends Manager<Player, LanguageInventory> {
    public InventoriesManager(LanguageLib plugin) {
        super(plugin);
    }

    public void handleOpen(Player player, LanguageInventory gui) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        add(player, gui, false);
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!values.containsKey(player)) return;

        event.setCancelled(true);
        values.get(player).onClick(event);
    }

    public void handleClose(Player player) {
        remove(player, gui -> {}, false);
    }

    @Override
    public void shutdown() {
        for (Player player : values.keySet()) {
            player.closeInventory();
        }
        super.shutdown();
    }
}