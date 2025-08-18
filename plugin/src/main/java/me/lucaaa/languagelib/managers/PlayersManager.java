package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.PlayerData;
import org.bukkit.entity.Player;

public class PlayersManager extends Manager<Player, PlayerData> {
    public PlayersManager(LanguageLib plugin) {
        super(plugin);
    }

    public void addPlayer(Player player) {
        add(player, new PlayerData(plugin, player));
    }

    public void removePlayer(Player player) {
        remove(player, playerData -> {
            plugin.getPluginMessagesManager().removeMessageable(player);
            plugin.getApiProvider().onLeave(player);
        });
    }

    public void reload() {
        for (PlayerData playerData : values.values()) {
            playerData.reload();
        }
    }
}