package me.lucaaa.languagelib.v1_13_R2;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.lucaaa.languagelib.common.HeadParser;
import me.lucaaa.languagelib.common.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class LegacyHeadParser implements HeadParser {
    private final Logger logger;

    public LegacyHeadParser(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ItemStack createBase64Head(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(item.getItemMeta());

        GameProfile profile = new GameProfile(UUID.randomUUID(), "name");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            logger.logError(Level.WARNING, "An error occurred while parsing a head! Head value: " + base64, error);
            return null;
        }

        item.setItemMeta(skull);
        return item;
    }

    @Override
    public ItemStack createPlayerHead(String player) {
        try {
            String UUIDJson = HeadParser.getJSONRequest("https://api.mojang.com/users/profiles/minecraft/" + player);
            JsonObject uuidObject = HeadParser.PARSER.parse(UUIDJson).getAsJsonObject();
            String dashlessUuid = uuidObject.get("id").getAsString();

            String profileJson = HeadParser.getJSONRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid);
            JsonObject profileObject = HeadParser.PARSER.parse(profileJson).getAsJsonObject();
            String base64 = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();

            return createBase64Head(base64);

        } catch (Exception e) {
            logger.logError(Level.WARNING, "An error occurred while parsing a player head! Head value: " + player, e);
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    @Override
    public boolean isTextureEqual(ItemStack head, ItemStack head2) {
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(head.getItemMeta());
        SkullMeta skull2 = (SkullMeta) Objects.requireNonNull(head2.getItemMeta());

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            Field profileField2 = skull2.getClass().getDeclaredField("profile");
            profileField2.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(skull);
            Property textureProperty = profile.getProperties().get("textures").iterator().next();
            GameProfile profile2 = (GameProfile) profileField2.get(skull2);
            Property textureProperty2 = profile2.getProperties().get("textures").iterator().next();

            return textureProperty.getValue().equals(textureProperty2.getValue());

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchElementException e) {
            logger.logError(Level.WARNING, "An error occurred while comparing textures!", e);
            return false;
        }
    }

    @Override
    public boolean hasTexture(ItemStack head) {
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(head.getItemMeta());

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(skull);
            return profile != null && !profile.getProperties().get("textures").isEmpty();

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchElementException e) {
            logger.logError(Level.WARNING, "An error occurred while comparing textures!", e);
            return false;
        }
    }
}