package me.lucaaa.languagelib.api.events;

import me.lucaaa.languagelib.api.language.Language;
import org.bukkit.entity.Player;

public class AsyncPlayerLanguageLoadEventImpl extends AsyncPlayerLanguageLoadEvent {
    private final Player player;
    private final Language language;

    public AsyncPlayerLanguageLoadEventImpl(Player player, Language language) {
        this.player = player;
        this.language = language;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Language getLanguage() {
        return language;
    }
}