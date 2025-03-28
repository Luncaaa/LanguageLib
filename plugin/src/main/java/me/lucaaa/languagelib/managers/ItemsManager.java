package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.data.MessageableImpl;
import me.lucaaa.languagelib.data.configs.Config;
import me.lucaaa.languagelib.data.configs.ItemConfig;
import me.lucaaa.languagelib.data.configs.Language;
import me.lucaaa.languagelib.utils.ProvidedConfig;
import me.lucaaa.languagelib.utils.SpecialStacks;
import me.lucaaa.languagelib.v1_18_R1.HeadUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ItemsManager extends Manager<String, ItemConfig> {
    private final boolean useNewHeads;
    private final Map<String, ItemStack> heads = new HashMap<>();
    private final ItemStack NOT_FOUND;
    private final BiConsumer<String, Throwable> onError;

    public ItemsManager(LanguageLib plugin, boolean useNewHeads) {
        super(plugin);
        this.useNewHeads = useNewHeads;
        this.onError = (message, error) -> plugin.logError(Level.WARNING, message, error);

        // Save default item config files.
        for (ProvidedConfig items : ProvidedConfig.values()) {
            Config.saveConfig(plugin, "items", items.getFileName());
        }

        File langDir = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "items");
        for (File file : Objects.requireNonNull(langDir.listFiles())) {
            add(file.getName(), new ItemConfig(plugin, file.getName(), useNewHeads));
        }

        // Loads item that is used when item is not found
        ItemStack notFound = new ItemStack(Material.BARRIER);
        ItemMeta meta = Objects.requireNonNull(notFound.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "Item not found!");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Check the default config to see what you are missing.");
        meta.setLore(lore);
        notFound.setItemMeta(meta);
        this.NOT_FOUND = notFound;
    }

    public ItemStack getItem(Messageable messageable, String key, Map<String, String> placeholders) {
        ItemConfig.Item item = getItemOrDefault(messageable, key);
        if (item == null) return NOT_FOUND;

        return parseItem(item, messageable, placeholders);
    }

    public void cacheHead(String key, String base64) {
        CompletableFuture.runAsync(() ->
                heads.put(key, HeadUtils.createTexturedHead(base64, !useNewHeads, onError))
        );
    }

    public ItemStack getHead(Messageable messageable, String key, Map<String, String> placeholders, String alternativeItem) {
        // The cached head itemStack (doesn't have name, lore or enchanted value).
        ItemStack cachedHead = heads.get(key);
        // The name, lore and enchanted value set in the items config file (maybe a different material).
        ItemConfig.Item headSettings = getItemOrDefault(messageable, alternativeItem);

        if (headSettings == null) return NOT_FOUND;

        // If the item set in the items config file is not a head or the base64 value doesn't match, parse the item directly.
        ItemStack headStack = headSettings.itemStack;
        if (headStack.getType() != Material.PLAYER_HEAD ||
                (HeadUtils.hasTexture(headStack, !useNewHeads, onError) &&
                        !HeadUtils.isTextureEqual(cachedHead, headStack, !useNewHeads, onError))) {
            return parseItem(headSettings, messageable, placeholders);
        }

        // Otherwise, parse the cached head with the item's settings.
        ItemConfig.Item parsedHead = new ItemConfig.Item(cachedHead, headSettings.name, headSettings.lore, headSettings.enchanted, headSettings.customModelData);

        return parseItem(parsedHead, messageable, placeholders);
    }

    private ItemStack parseItem(ItemConfig.Item item, Messageable messageable, Map<String, String> placeholders) {
        ItemStack itemStack = item.itemStack;
        if (itemStack instanceof SpecialStacks) {
            return itemStack;
        }

        MessagesManagerImpl messagesManager = plugin.getManager(MessagesManagerImpl.class);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (item.name != null) {
                meta.setDisplayName(messagesManager.toLegacy(messageable, item.name, placeholders));
            }

            List<String> lore = item.lore.stream().map(line -> messagesManager.toLegacy(messageable, line, placeholders)).collect(Collectors.toList());
            meta.setLore(lore);

            if (item.enchanted) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            }

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private ItemConfig.Item getItemOrDefault(Messageable messageable, String key) {
        MessagesManagerImpl messagesManager = plugin.getManager(MessagesManagerImpl.class);
        MessageableImpl messageableImpl = (MessageableImpl) messageable;
        Language language = messageableImpl.getLang();

        ItemConfig itemConfig = get(language.getFileName(), false);
        if (itemConfig == null) {
            language = messagesManager.getDefaultLang();
            plugin.log(Level.WARNING, "Items config file was not found for language \"" + messageableImpl.getLang().getFileName() + "\". Setting to default language. (Item: " + key + ")");

            ItemConfig newItemConfig = get(language.getFileName());
            if (newItemConfig == null) {
                language = messagesManager.get(ProvidedConfig.EN_US.getFileName());
                plugin.log(Level.WARNING, "Items config file was not found for default language \"" + messageableImpl.getLang().getFileName() + "\". Setting to " + language.getFileName() + ". (Item: " + key + ")");
            }
        }

        return get(language.getFileName()).getItem(key);
    }

    public static Material parseMaterial(String configName, Consumer<String> ifNull) {
        Material configMaterial = Material.getMaterial(configName.toUpperCase());
        if (configMaterial == null) {
            ifNull.accept(configName);
            return Material.BARRIER;
        } else {
            return configMaterial;
        }
    }
}