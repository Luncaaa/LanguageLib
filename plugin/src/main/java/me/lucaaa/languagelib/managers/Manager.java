package me.lucaaa.languagelib.managers;

import me.lucaaa.languagelib.LanguageLib;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Manager<K, T> {
    protected final LanguageLib plugin;
    protected final Map<K, T> values = new HashMap<>();

    public Manager(LanguageLib plugin) {
        this.plugin = plugin;
    }

    public void add(K toAdd, T value) {
        add(toAdd, value, true);
    }

    public void add(K toAdd, T value, boolean log) {
        if (values.containsKey(toAdd)) {
            if (log) plugin.log(Level.WARNING, getClass().getSimpleName() + " tried a registering an element that already existed: " + toAdd);
        } else {
            values.put(toAdd, value);
        }
    }

    public void remove(K toRemove, Consumer<T> ifExists) {
        T removed = values.remove(toRemove);
        if (removed == null) {
            plugin.log(Level.WARNING, getClass().getSimpleName() + " tried to remove a non-existent element: " + toRemove);
        } else {
            ifExists.accept(removed);
        }
    }

    public T get(K toGet) {
        return get(toGet, true);
    }

    public T get(K toGet, boolean log) {
        T value = values.get(toGet);
        if (value == null) {
            if (log) plugin.log(Level.WARNING, getClass().getSimpleName() + " tried to fetch a non-existent element: " + toGet);
        }
        return value;
    }

    public void shutdown() {
        values.clear();
    }
}