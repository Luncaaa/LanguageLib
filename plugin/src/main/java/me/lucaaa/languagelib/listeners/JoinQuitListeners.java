package me.lucaaa.languagelib.listeners;

import me.lucaaa.languagelib.LanguageLib;
import me.lucaaa.languagelib.managers.PlayersManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListeners implements Listener {
    private final LanguageLib plugin;

    public JoinQuitListeners(LanguageLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getManager(PlayersManager.class).addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.getManager(PlayersManager.class).removePlayer(event.getPlayer());
    }
}