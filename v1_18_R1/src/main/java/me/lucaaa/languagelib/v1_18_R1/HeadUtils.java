package me.lucaaa.languagelib.v1_18_R1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class HeadUtils {
    public static ItemStack createTexturedHead(String base64, boolean isPre1_18, BiConsumer<String, Throwable> onError) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(item.getItemMeta());

        if (isPre1_18) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "name");
            profile.getProperties().put("textures", new Property("textures", base64));

            try {
                Field profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skull, profile);

            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                onError.accept("An error occurred while parsing a head! Head value: " + base64, error);
                return null;
            }

        } else {
            try {
                String skinJson = new String(Base64.getDecoder().decode(base64));
                JsonObject skinObject = JsonParser.parseString(skinJson).getAsJsonObject();
                String url = skinObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                URL urlObject = new URI(url).toURL();
                textures.setSkin(urlObject);
                profile.setTextures(textures);
                skull.setOwnerProfile(profile);

            } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
                onError.accept("An error occurred while parsing a head! Head value: " + base64, e);
                return item;
            }
        }

        item.setItemMeta(skull);
        return item;
	}

    public static ItemStack createPlayerHead(String player, boolean isPre1_18, BiConsumer<String, Throwable> onError) {
        try {
            String UUIDJson = IOUtils.toString(new URI("https://api.mojang.com/users/profiles/minecraft/" + player).toURL(), StandardCharsets.UTF_8);
            JsonObject uuidObject = JsonParser.parseString(UUIDJson).getAsJsonObject();
            String dashlessUuid = uuidObject.get("id").getAsString();

            String profileJson = IOUtils.toString(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid).toURL(), StandardCharsets.UTF_8);
            JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
            String base64 = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            return createTexturedHead(base64, isPre1_18, onError);

        } catch (IOException | URISyntaxException e) {
            onError.accept("The player name " + player + " does not exist!", e);
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    public static boolean isTextureEqual(ItemStack head, ItemStack head2, boolean isPre1_18, BiConsumer<String, Throwable> onError) {
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(head.getItemMeta());
        SkullMeta skull2 = (SkullMeta) Objects.requireNonNull(head2.getItemMeta());

        if (isPre1_18) {
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
                onError.accept("An error occurred while comparing textures!", e);
                return false;
            }

        } else {
            PlayerProfile itemProfile = skull.getOwnerProfile();
            if (itemProfile == null) return false;
            PlayerTextures itemTextures = itemProfile.getTextures();
            URL itemSkinUrl = itemTextures.getSkin();
            if (itemSkinUrl == null) return false;

            PlayerProfile itemProfile2 = skull2.getOwnerProfile();
            if (itemProfile2 == null) return false;
            PlayerTextures itemTextures2 = itemProfile2.getTextures();
            URL itemSkinUrl2 = itemTextures2.getSkin();
            if (itemSkinUrl2 == null) return false;

            return itemSkinUrl.toString().equals(itemSkinUrl2.toString());
        }
    }

    public static boolean hasTexture(ItemStack head, boolean isPre1_18, BiConsumer<String, Throwable> onError) {
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(head.getItemMeta());

        if (isPre1_18) {
            try {
                Field profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);

                GameProfile profile = (GameProfile) profileField.get(skull);
                return profile != null && !profile.getProperties().get("textures").isEmpty();

            } catch (NoSuchFieldException | IllegalAccessException | NoSuchElementException e) {
                onError.accept("An error occurred while comparing textures!", e);
                return false;
            }

        } else {
            PlayerProfile itemProfile = skull.getOwnerProfile();
            if (itemProfile == null) return false;
            PlayerTextures itemTextures = itemProfile.getTextures();
            URL itemSkinUrl = itemTextures.getSkin();
            return itemSkinUrl != null;
        }
    }
}