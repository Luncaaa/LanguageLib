package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.data.PlayerData;
import me.lucaaa.languagelib.data.configs.Config;
import me.lucaaa.languagelib.data.configs.Language;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.languagelib.data.MessageableImpl;
import me.lucaaa.languagelib.utils.NoLanguagesFoundException;
import me.lucaaa.languagelib.utils.ProvidedConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessagesManagerImpl extends Manager<String, Language> implements MessagesManager {
    private final String prefix;
    private final Language defaultLang;
    private final String fullLanguageFolderPath;

    private final Map<CommandSender, Messageable> messageables = new HashMap<>();

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final BungeeComponentSerializer bungeeSerializer = BungeeComponentSerializer.get();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public MessagesManagerImpl(LanguageLib plugin, Plugin apiPlugin, String prefix, String languagesFolderPath, boolean isNotAPI) {
        super(plugin);
        this.prefix = prefix;
        this.fullLanguageFolderPath = apiPlugin.getDataFolder().getAbsolutePath() + File.separator + languagesFolderPath;

        // Save default languages
        if (isNotAPI) {
            for (ProvidedConfig lang : ProvidedConfig.values()) {
                Config.saveConfig(plugin, "langs", lang.getFileName());
            }
        }

        String errorMessage = "No files found in directory";
        File langDir = new File(fullLanguageFolderPath);
        if (!langDir.exists() || !langDir.isDirectory()) {
            plugin.logError(
                    Level.SEVERE,
                    "Plugin \"" + apiPlugin.getName() + "\" provided an invalid directory.",
                    new NotDirectoryException("Not a valid directory: " + fullLanguageFolderPath));
        }

        for (File file : Objects.requireNonNull(langDir.listFiles())) {
            if (!isValidName(Config.getNameWithoutExtension(file))) {
                errorMessage = "Files were found but with invalid name";
                plugin.log(Level.WARNING, "=========================");
                plugin.log(Level.WARNING, "File \"" + file.getName() + "\" has an invalid name in " + fullLanguageFolderPath);
                plugin.log(Level.WARNING, "It must have the following format: \"xx_xx\", where \"x\" is a lower-case letter.");
                plugin.log(Level.WARNING, "=========================");
                continue;
            }
            add(file.getName(), new Language(plugin, apiPlugin.getDataFolder().getAbsolutePath(), languagesFolderPath, file.getName(), isNotAPI));

            if (!isNotAPI) {
                Set<String> pluginLanguages = plugin.getManager(this.getClass()).getLanguagesNames();
                if (!pluginLanguages.contains(file.getName())) {
                    plugin.log(Level.WARNING, "=========================");
                    plugin.log(Level.WARNING, "Plugin \"" + apiPlugin.getName() + "\" tried to register \"" + file.getName() + "\" language, but the plugin doesn't have such language.");
                    plugin.log(Level.WARNING, "Path to file: " + fullLanguageFolderPath);
                    plugin.log(Level.WARNING, "=========================");
                }
            }
        }

        if (isNotAPI) {
            String defaultLang = plugin.getMainConfig().defaultLang;
            Language defLang = get(defaultLang);
            if (defLang == null) {
                defLang = get(ProvidedConfig.EN_US.getFileName());
                plugin.log(Level.WARNING, "Default language file \"" + defaultLang + "\" was not found. Switching to \"" + defLang + "\".");
            }
            this.defaultLang = defLang;
        } else {
            this.defaultLang = null;
        }

        if (values.isEmpty()) {
            plugin.logError(Level.SEVERE, errorMessage, new NoLanguagesFoundException("No valid languages were found in " + fullLanguageFolderPath));
        }
    }

    public void sendMessage(CommandSender sender, String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend =  prefix + " " + messageToSend;

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messageToSend));
    }

    @Override
    public void sendMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        Component message = getMessage(messageable, key, placeholders, addPrefix);
        BaseComponent[] component = bungeeSerializer.serialize(message);
        ((MessageableImpl) messageable).getSender().spigot().sendMessage(component);
    }

    @Override
    public List<String> getList(Messageable messageable, String key, Map<String, String> placeholders) {
        List<String> unparsed = getUnparsedList(messageable, key);
        return unparsed.stream().map(line -> legacySerializer.serialize(parseMessage((MessageableImpl) messageable, line, placeholders, false))).collect(Collectors.toList());
    }

    @Override
    public List<String> getUnparsedList(Messageable messageable, String key) {
        return getLanguage(messageable).getList(key);
    }

    @Override
    public String getUnparsedMessage(Messageable messageable, String key) {
        return getLanguage(messageable).getMessage(key);
    }

    private Component getMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        return parseMessage((MessageableImpl) messageable, getLanguage(messageable).getMessage(key), placeholders, addPrefix);
    }

    @Override
    public String toLegacy(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        return legacySerializer.serialize(getMessage(messageable, key, placeholders, addPrefix));
    }

    public String toLegacy(Messageable messageable, String message, Map<String, String> placeholders) {
        return legacySerializer.serialize(parseMessage((MessageableImpl) messageable, message, placeholders, false));
    }

    private Component parseMessage(MessageableImpl messageable, String message, Map<String, String> placeholders, boolean addPrefix) {
        if (addPrefix) message = prefix + " &r" + message;

        if (placeholders != null) {
            message = parsePlaceHolders(message, placeholders);
        }

        if (messageable instanceof PlayerData) {
            PlayerData playerData = (PlayerData) messageable;
            message = parsePlaceHolders(message, playerData.getPlaceholders());
        }

        if (plugin.isPapiInstalled() && messageable.isPlayer()) {
            Player player = (Player) messageable.getSender();
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        // From legacy and minimessage format to a component
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = miniMessage.serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        return miniMessage.deserialize(minimessage);
        // From Minimessage component to legacy string.
        // sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(component));

        // Use to get legacy text from the component.
        // return TextComponent.toLegacyText(BungeeComponentSerializer.get().serialize(component));
    }

    private String parsePlaceHolders(String message, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    @Override
    public Messageable getMessageable(CommandSender sender) {
        return messageables.computeIfAbsent(sender, s -> {
            MessageableImpl messageable;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                messageable = plugin.getManager(PlayersManager.class).get(player);
            } else {
                messageable = plugin.getServerConsole();
            }

            return messageable.withManager(this);
        });
    }

    @Override
    public Messageable getServerConsole() {
        return plugin.getServerConsole();
    }

    public Language getDefaultLang() {
        return defaultLang;
    }

    public Set<String> getLanguagesNames() {
        return values.keySet();
    }

    public Collection<Language> getLanguages() {
        return values.values();
    }

    private boolean isValidName(String name) {
        return Pattern.matches("^[a-z]{2}_[a-z]{2}$", name);
    }

    private Language getLanguage(Messageable messageable) {
        MessageableImpl messageableImpl = (MessageableImpl) messageable;
        Language lang = get(messageableImpl.getLang().getFileName());
        if (lang == null) {
            Optional<Language> first = values.values().stream().findFirst();
            if (first.isPresent()) {
                return first.get();
            } else {
                throw new NoLanguagesFoundException("No valid languages in " + fullLanguageFolderPath);
            }
        } else {
            return lang;
        }
    }

    public void onLeave(Player player) {
        messageables.remove(player);
    }
}