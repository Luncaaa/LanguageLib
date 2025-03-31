package me.lucaaa.languagelib.data.configs;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.managers.ItemsManager;
import me.lucaaa.languagelib.utils.SpecialStacks;
import me.lucaaa.languagelib.v1_18_R1.HeadUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ItemConfig extends Config {
    private final boolean useNewHeads;
    private final Map<String, Item> items = new HashMap<>();

    public ItemConfig(LanguageLib plugin, boolean useNewHeads) {
        super(plugin, "items.yml", false);
        this.useNewHeads = useNewHeads;

        // Each key that is not a config section is added to the map along with its corresponding message
        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;

            if (key.endsWith("material")) {
                String sectionName = key.substring(0, key.lastIndexOf('.'));
                if (items.containsKey(sectionName)) continue;

                loadItem(sectionName, Objects.requireNonNull(config.getConfigurationSection(sectionName)));
            }
        }
    }

    private void loadItem(String key, ConfigurationSection itemSection) {
        String configMaterial = getOrDefault(key + ".material", Material.BARRIER.name());
        Material material;
        ItemStack itemStack;

        if (configMaterial.equalsIgnoreCase("EMPTY")) {
            material = Material.AIR;
            itemStack = new SpecialStacks.EmptyStack();
        } else {
            material = ItemsManager.parseMaterial(configMaterial, (materialName) ->
                    plugin.log(Level.WARNING, "The material \"" + materialName + "\" specified for the item \"" + itemSection.getName() + "\" does not exist. Setting to barrier by default.")
            );
            itemStack = new ItemStack(material);
        }

        String name = itemSection.getString("name");
        String lore = itemSection.getString("lore");
        boolean enchanted = itemSection.getBoolean("enchanted", false);
        int customModelData = itemSection.getInt("customModelData", 0);

        if (material == Material.PLAYER_HEAD) {
            ConfigurationSection headSection = itemSection.getConfigurationSection("head");
            if (headSection != null) {
                if (headSection.isString("base64") && !Objects.requireNonNull(headSection.getString("base64", "")).contains("%")) {
                    itemStack = HeadUtils.createTexturedHead(headSection.getString("base64"), !useNewHeads, (message, error) -> plugin.logError(Level.WARNING, message, error));
                } else if (headSection.isString("player")) {
                    itemStack = HeadUtils.createPlayerHead(headSection.getString("player"), !useNewHeads, (message, error) -> plugin.logError(Level.WARNING, message, error));
                }
            }
        }

        items.put(key, new Item(itemStack, name, lore, enchanted, customModelData));
    }

    public Item getItem(String key) {
        if (!items.containsKey(key)) {
            plugin.log(Level.WARNING, "Key \"" + key + "\" not found for items \"" + file.getName() + "\"!");
            return null;
        }

        return items.get(key);
    }

    public static class Item {
        public final ItemStack itemStack;
        public final String name;
        public final String lore;
        public final boolean enchanted;
        public final int customModelData;

        public Item(ItemStack itemStack, String name, String lore, boolean enchanted, int customModelData) {
            this.itemStack = itemStack;
            this.name = name;
            this.lore = lore;
            this.enchanted = enchanted;
            this.customModelData = customModelData;
        }
    }
}