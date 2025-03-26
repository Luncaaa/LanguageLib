package me.lucaaa.languagelib.utils;

public enum ProvidedConfig {
    EN_US("en_us.yml");

    private final String fileName;

    ProvidedConfig(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}