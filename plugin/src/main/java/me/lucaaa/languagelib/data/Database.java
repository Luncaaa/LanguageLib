package me.lucaaa.languagelib.data;

public class Database {
    public final boolean useMySQL;
    public final String name;
    public final String host;
    public final String port;
    public final String username;
    public final String password;

    public Database(
            boolean useMySQL,
            String name,
            String host,
            String port,
            String username,
            String password
    ) {
        this.useMySQL = useMySQL;
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}