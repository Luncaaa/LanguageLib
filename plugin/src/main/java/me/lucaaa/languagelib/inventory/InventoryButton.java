package me.lucaaa.languagelib.inventory;

import me.lucaaa.languagelib.utils.SpecialStacks;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryButton {
    private final ItemStack item;

    public InventoryButton(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public abstract void onClick(InventoryClickEvent event);

    public static class Empty extends InventoryButton {
        public Empty() {
            super(new SpecialStacks.EmptyFiller());
        }

        @Override
        public void onClick(InventoryClickEvent event) {}
    }
}