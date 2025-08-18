package me.lucaaa.languagelib.managers.messages;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.api.language.Messageable;
import me.lucaaa.languagelib.api.language.MessagesManager;
import me.lucaaa.languagelib.data.configs.Config;
import me.lucaaa.languagelib.data.configs.LanguageImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.languagelib.data.LangProvider;
import me.lucaaa.languagelib.data.MessageableImpl;
import me.lucaaa.languagelib.managers.Manager;
import me.lucaaa.languagelib.utils.NoLanguagesFoundException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessagesManagerImpl extends Manager<String, LanguageImpl> implements MessagesManager {
    private final JavaPlugin apiPlugin; // Used for the name of the plugin that "owns" this MessagesManagerImpl instance.
    private final boolean isMain;
    private final String languagesFolderPath;
    protected final String fullLanguageFolderPath;

    private final String prefix;
    private final List<String> errors = new ArrayList<>();
    private LanguageImpl defLang;

    private final Map<CommandSender, Messageable> messageables = new HashMap<>();

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    // private final BungeeComponentSerializer bungeeSerializer = BungeeComponentSerializer.get();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public MessagesManagerImpl(LanguageLib plugin, JavaPlugin apiPlugin, boolean isMain, String prefix, String languagesFolderPath) {
        super(plugin);
        this.apiPlugin = apiPlugin;
        this.isMain = isMain;
        this.languagesFolderPath = languagesFolderPath;
        this.fullLanguageFolderPath = apiPlugin.getDataFolder().getAbsolutePath() + File.separator + languagesFolderPath;
        this.prefix = prefix;

        reload();
    }

    public void sendMessage(CommandSender sender, String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend =  prefix + " " + messageToSend;

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messageToSend));
    }

    @Override
    public void sendColored(Messageable messageable, String message) {
        Component msg = parseMessage(messageable, message, null, false);
        // Most proper way of sending components. If file size becomes an issue, use Bungee platform
        // Convert component to bungee component and use sender.spigot()
        if (((MessageableImpl) messageable).isPlayer()) {
            plugin.getAudience((Player) messageable.getSender()).sendMessage(msg);
        } else {
            plugin.getConsoleAudience().sendMessage(msg);
        }
    }

    @Override
    public void sendMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        Component message = getMessage(messageable, key, placeholders, addPrefix);
        // Most proper way of sending components. If file size becomes an issue, use Bungee platform
        // Convert component to bungee component and use sender.spigot()
        if (((MessageableImpl) messageable).isPlayer()) {
            plugin.getAudience((Player) messageable.getSender()).sendMessage(message);
        } else {
            plugin.getConsoleAudience().sendMessage(message);
        }
    }

    @Override
    public List<String> getList(Messageable messageable, String key, Map<String, String> placeholders) {
        List<String> unparsed = getUnparsedList(messageable, key);
        return unparsed.stream().map(line -> legacySerializer.serialize(parseMessage(messageable, line, placeholders, false))).collect(Collectors.toList());
    }

    @Override
    public List<String> getUnparsedList(Messageable messageable, String key) {
        return getLanguage(messageable).getList(key);
    }

    @Override
    public String getUnparsedMessage(Messageable messageable, String key) {
        return getLanguage(messageable).getMessage(key);
    }

    @Override
    public String getMessageMinimessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        return miniMessage.serialize(getMessage(messageable, key, placeholders, addPrefix));
    }

    private Component getMessage(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        return parseMessage(messageable, getLanguage(messageable).getMessage(key), placeholders, addPrefix);
    }

    @Override
    public String getMessageLegacy(Messageable messageable, String key, Map<String, String> placeholders, boolean addPrefix) {
        return legacySerializer.serialize(getMessage(messageable, key, placeholders, addPrefix));
    }

    private Component parseMessage(Messageable messageable, String message, Map<String, String> placeholders, boolean addPrefix) {
        if (addPrefix) message = prefix + " &r" + message;

        MessageableImpl messageableImpl = (MessageableImpl) messageable;
        Map<String, String> allPlaceholders = messageableImpl.getPlaceholders();
        if (placeholders != null) {
            allPlaceholders.putAll(placeholders);
        }

        message = parsePlaceHolders(message, allPlaceholders);

        if (plugin.isPapiInstalled() && messageableImpl.isPlayer()) {
            Player player = (Player) messageableImpl.getSender();
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
        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            throw new IllegalArgumentException("You can only get a Messageable instance for players and console!");
        }

        return messageables.computeIfAbsent(sender, s -> {
            LangProvider langProvider;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                langProvider = plugin.getPlayersManager().get(player);
            } else {
                langProvider = plugin.getServerConsole();
            }

            return new MessageableImpl(sender, this, langProvider);
        });
    }

    @Override
    public Messageable getServerConsole() {
        return getMessageable(plugin.getServer().getConsoleSender());
    }

    public Set<String> getLanguagesNames() {
        return values.keySet();
    }

    public Collection<LanguageImpl> getLanguages() {
        return values.values();
    }

    private boolean isNameNotValid(String name) {
        return !Pattern.matches("^[a-z]{2}_[a-z]{2}$", name);
    }

    private LanguageImpl getLanguage(Messageable messageable) {
        MessageableImpl messageableImpl = (MessageableImpl) messageable;
        LanguageImpl lang = get(messageableImpl.getLanguage().getFileName());
        if (lang == null) {
            if (defLang != null) {
                return defLang;
            } else {
                throw new NoLanguagesFoundException("No valid languages in " + fullLanguageFolderPath);
            }
        } else {
            return lang;
        }
    }

    public void removeMessageable(Player player) {
        messageables.remove(player);
    }

    private void addError(String pathToLang, String error) {
        errors.add("Error: " + error);
        if (pathToLang != null) {
            errors.add("Path to file: " + pathToLang);
        }
        errors.add(" ");
    }

    private void logErrors() {
        if (errors.isEmpty()) return;
        plugin.log(Level.WARNING, "=".repeat(25));
        plugin.log(Level.SEVERE, "Plugin \"" + apiPlugin.getName() + "\" caused an error when processing a language:");
        plugin.log(Level.WARNING, " ");
        for (String line : errors) {
            plugin.log(Level.WARNING, line);
        }
        plugin.log(Level.WARNING, "=".repeat(25));

    }

    @Override
    public void reload() {
        shutdown();
        errors.clear();

        String errorMessage = "No files found in directory";
        File langDir = new File(fullLanguageFolderPath);
        if (!langDir.exists() || !langDir.isDirectory()) {
            plugin.logError(
                    Level.SEVERE,
                    "Plugin \"" + apiPlugin.getName() + "\" provided an invalid directory.",
                    new NotDirectoryException("Not a valid directory: " + fullLanguageFolderPath));
        }

        for (File file : Objects.requireNonNull(langDir.listFiles())) {
            String name = Config.getNameWithoutExtension(file);

            if (plugin.getMainConfig().ignoredLanguages.contains(name)) {
                continue;
            }

            if (isNameNotValid(name)) {
                errorMessage = "Files were found but with invalid name";
                addError(fullLanguageFolderPath, "Invalid name - It must have the following format: \"xx_xx\", where \"x\" is a lower-case letter.");
                continue;
            }

            if (!isMain) {
                Set<String> pluginLanguages = plugin.getPluginMessagesManager().getLanguagesNames();
                if (!pluginLanguages.contains(file.getName())) {
                    addError(file.getAbsolutePath(), plugin.getName() + " doesn't have such language (or it's ignored), so it'll be unavailable.");
                    continue;
                }
            }

            add(file.getName(), new LanguageImpl(plugin, apiPlugin, languagesFolderPath, file.getName(), isMain));
        }

        // If the plugin has no languages or has more languages than LanguageLib, log a warning.
        if (values.isEmpty()) {
            plugin.logError(Level.SEVERE, errorMessage, new NoLanguagesFoundException("No valid languages were found in " + fullLanguageFolderPath));

        } else if (!isMain) {
            for (String lang : plugin.getPluginMessagesManager().getLanguagesNames()) {
                if (get(lang, false) == null) {
                    addError(fullLanguageFolderPath, plugin.getName() + " has the language \"" + lang + "\", but \"" + apiPlugin.getName() + "\" doesn't have it." );
                }
            }
        }

        if (!isMain) {
            String defLangName = plugin.getPluginMessagesManager().getDefaultLang().getFileName();
            defLang = get(defLangName, false);
            // If the default language is not found, use the first file found.
            if (defLang == null) {
                Optional<LanguageImpl> first = values.values().stream().findFirst();
                if (first.isPresent()) {
                    defLang = first.get();
                    addError(null, "Default language file \"" + defLangName + "\" was not found. Switching to \"" + defLang + "\".");
                } else {
                    throw new NoLanguagesFoundException("No valid languages in " + fullLanguageFolderPath);
                }
            }
        }

        logErrors();
    }
}