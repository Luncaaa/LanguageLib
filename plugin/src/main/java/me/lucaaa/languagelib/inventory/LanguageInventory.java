package me.lucaaa.languagelib.inventory;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.data.MessageableImpl;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.lucaaa.languagelib.managers.InventoriesManager;
import me.lucaaa.languagelib.managers.ItemsManager;
import me.lucaaa.languagelib.managers.MessagesManagerImpl;
import me.lucaaa.languagelib.managers.PlayersManager;
import me.lucaaa.languagelib.utils.SpecialStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LanguageInventory {
    protected final LanguageLib plugin;
    protected final ItemsManager itemsManager;
    protected final MessagesManagerImpl messagesManager;

    private final Map<Integer, InventoryButton> buttons = new HashMap<>();
    private boolean loaded = false;
    private final Inventory inventory;
    protected final Messageable viewer;
    private final List<LanguageImpl> elements;

    private final int pageIndex;
    private final int rows;
    private final int startIndex;

    private final boolean actionRow;
    private final boolean nextPage;
    private final int pagesNumber;

    private final LanguageInventory previous;

    public LanguageInventory(LanguageLib plugin, Messageable viewer) {
        this(plugin, viewer, 0, new ArrayList<>(plugin.getManager(MessagesManagerImpl.class).getLanguages()), null);
    }

    public LanguageInventory(LanguageLib plugin, Messageable viewer, int pageIndex, List<LanguageImpl> elements, LanguageInventory previous) {
        this.plugin = plugin;
        this.itemsManager = plugin.getManager(ItemsManager.class);
        this.messagesManager = plugin.getManager(MessagesManagerImpl.class);

        this.inventory = Bukkit.createInventory(
                null,
                9 * getRows(elements.size()),
                messagesManager.getMessageLegacy(viewer, "inventory.title", getPlaceholders(pageIndex, elements.size()), false)
        );
        this.viewer = viewer;
        this.elements = elements;

        this.pageIndex = pageIndex;
        this.rows = getRows(elements.size());
        this.previous = previous;

        // Page number * 28 slots per GUI
        this.startIndex = pageIndex * 28;
        if (pageIndex == 0 && elements.size() <= 28) { // 28 slots without counting the margins.
            this.actionRow = false;
            this.nextPage = false;
        } else {
            this.actionRow = true;
            this.nextPage = (elements.size() - 1) > startIndex + 27;
        }

        this.pagesNumber = (int) Math.ceil((double) elements.size() / 28);
    }

    public void decorate() {
        addButton(8, new InventoryButton(itemsManager.getItem(viewer, "info", getPlaceholders())) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        });

        int index = startIndex;
        for (int row = 1; row < rows - 1 ; row++) {
            for (int slot = 1; slot < 8; slot++) {
                int slotIndex = (row * 9) + slot;

                if (index == elements.size()) {
                    addButton(slotIndex, new InventoryButton.Empty());
                    continue;
                }

                addButton(slotIndex, parseButton(elements.get(index)));
                index++;
            }
        }

        if (!actionRow) return;

        // Previous button
        if (previous != null) {
            addButton(46, new InventoryButton(itemsManager.getItem(viewer, "previous_page", getPlaceholders())) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    plugin.getManager(InventoriesManager.class).handleOpen((Player) event.getWhoClicked(), previous);
                }
            });
        }

        // Page info button
        addButton(49, new InventoryButton(itemsManager.getItem(viewer, ".current_page", getPlaceholders())) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        });

        // Next page button
        if (nextPage) {
            addButton(52, new InventoryButton(itemsManager.getItem(viewer, ".next_page", getPlaceholders())) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    plugin.getManager(InventoriesManager.class).handleOpen((Player) event.getWhoClicked(), copy(pageIndex + 1, LanguageInventory.this));
                }
            });
        }
    }

    protected void reopen() {
        Player player = (Player) ((MessageableImpl) viewer).getSender();
        plugin.getManager(InventoriesManager.class).handleOpen(player, copy(pageIndex, previous));
    }

    private InventoryButton parseButton(LanguageImpl element) {
        Map<String, String> placeholders = getPlaceholders();
        Player player = (Player) ((MessageableImpl) viewer).getSender();
        PlayerData playerData = plugin.getManager(PlayersManager.class).get(player);

        String selected;
        if (element.equals(playerData.getLang())) {
            selected = messagesManager.getUnparsedMessage(viewer, "inventory.selected_placeholder.selected");
        } else {
            selected = messagesManager.getUnparsedMessage(viewer, "inventory.selected_placeholder.unselected");
        }

        placeholders.put("%selected%", selected);
        placeholders.put("%name%", element.getName());
        placeholders.put("%code%", element.getCode());

        ItemStack itemStack = plugin.getManager(ItemsManager.class).getHead(viewer, element.getFileName(), placeholders, "language");
        // Only selected language will be enchanted.
        if (element.equals(playerData.getLang())) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(meta);
            }
        } else {
            for(Enchantment enchantment : itemStack.getEnchantments().keySet()){
                itemStack.removeEnchantment(enchantment);
            }
        }

        return new InventoryButton(itemStack) {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (element.equals(playerData.getLang())) {
                    viewer.sendMessage("commands.language.already_selected", null);
                } else {
                    playerData.setLang(element, false);
                    reopen();
                    viewer.sendMessage("commands.language.success", null);
                }
            }
        };
    }

    private static int getRows(int size) {
        int rows = (int) Math.ceil((double) size / 7) + 2; // 7 free slots per row (other are borders) + 2 top and bottom borders.
        if (rows > 6) rows = 6;
        return rows;
    }

    // For constructor because you cannot set variables before calling super...
    private static Map<String, String> getPlaceholders(int pageIndex, int elementsSize) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%page%", String.valueOf(pageIndex + 1));
        placeholders.put("%total_pages%", String.valueOf((int) Math.ceil((double) elementsSize / 28)));
        return placeholders;
    }

    protected Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%page%", String.valueOf(pageIndex + 1));
        placeholders.put("%total_pages%", String.valueOf(pagesNumber));
        return placeholders;
    }

    private LanguageInventory copy(int pageIndex, LanguageInventory previous) {
        return new LanguageInventory(plugin, viewer, pageIndex, elements, previous);
    }

    protected ItemStack getFiller() {
        return plugin.getManager(ItemsManager.class).getItem(viewer, "border", getPlaceholders());
    }

    public void onClick(InventoryClickEvent event) {
        if (!buttons.containsKey(event.getSlot())) return;

        buttons.get(event.getSlot()).onClick(event);
    }

    protected void addButton(int slot, InventoryButton button) {
        buttons.put(slot, button);
    }

    private void setButtons() {
        ItemStack filler = getFiller();

        for (int i = 0; i < getInventory().getSize(); i++) {
            InventoryButton button = buttons.get(i);

            if (button != null && !(button.getItem() instanceof SpecialStacks.EmptyStack)) {
                if (button.getItem() instanceof SpecialStacks.EmptyFiller) {
                    inventory.setItem(i, new ItemStack(Material.AIR));
                } else {
                    inventory.setItem(i, button.getItem());
                }
                continue;
            }

            getInventory().setItem(i, filler);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void onOpen() {
        if (loaded) return;
        decorate();
        setButtons();
        loaded = true;
    }
}