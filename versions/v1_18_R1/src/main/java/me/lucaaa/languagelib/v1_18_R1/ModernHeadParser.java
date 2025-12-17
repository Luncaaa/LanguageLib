package me.lucaaa.languagelib.v1_18_R1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lucaaa.languagelib.common.HeadParser;
import me.lucaaa.languagelib.common.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ModernHeadParser implements HeadParser {
    private final Logger logger;

    public ModernHeadParser(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ItemStack createBase64Head(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(item.getItemMeta());

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
            logger.logError(Level.WARNING, "An error occurred while parsing a head! Head value: " + base64, e);
            return item;
        }

        item.setItemMeta(skull);
        return item;
    }

    @Override
    public ItemStack createPlayerHead(String player) {
        try {
            String UUIDJson = HeadParser.getJSONRequest("https://api.mojang.com/users/profiles/minecraft/" + player);
            JsonObject uuidObject = JsonParser.parseString(UUIDJson).getAsJsonObject();
            String dashlessUuid = uuidObject.get("id").getAsString();

            String profileJson = HeadParser.getJSONRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid);
            JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
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

    @Override
    public boolean hasTexture(ItemStack head) {
        SkullMeta skull = (SkullMeta) Objects.requireNonNull(head.getItemMeta());

        PlayerProfile itemProfile = skull.getOwnerProfile();
        if (itemProfile == null) return false;
        PlayerTextures itemTextures = itemProfile.getTextures();
        URL itemSkinUrl = itemTextures.getSkin();
        return itemSkinUrl != null;
    }
}