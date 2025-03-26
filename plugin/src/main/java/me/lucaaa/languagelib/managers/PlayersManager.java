package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.data.PlayerData;
import org.bukkit.entity.Player;

public class PlayersManager extends Manager<Player, PlayerData> {
    public PlayersManager(LanguageLib plugin) {
        super(plugin);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    public void addPlayer(Player player) {
        add(player, new PlayerData(plugin, player));
    }

    public void removePlayer(Player player) {
        remove(player, playerData -> playerData.saveData(true));
    }

    @Override
    public void shutdown() {
        for (PlayerData playerData : values.values()) {
            playerData.saveData(false);
        }
        super.shutdown();
    }
}