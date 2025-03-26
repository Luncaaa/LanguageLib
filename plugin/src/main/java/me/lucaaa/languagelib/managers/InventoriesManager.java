package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.inventory.LanguageInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

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
        Player player = (Player) event.getWhoClicked();
        if (!values.containsKey(player)) return;

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;

        event.setCancelled(true);
        get(player).onClick(event);
    }

    public void handleClose(Player player) {
        remove(player, gui -> {});
    }

    @Override
    public void shutdown() {
        for (Player player : values.keySet()) {
            player.closeInventory();
        }
        super.shutdown();
    }
}