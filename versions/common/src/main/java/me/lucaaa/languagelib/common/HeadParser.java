package me.lucaaa.languagelib.common;

import com.google.gson.JsonParser;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public interface HeadParser {
    JsonParser PARSER = new JsonParser();

    ItemStack createBase64Head(String base64);

    ItemStack createPlayerHead(String player);

    boolean isTextureEqual(ItemStack head, ItemStack head2);

    boolean hasTexture(ItemStack head);

    static String getJSONRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 200 = full success
        if (response.statusCode() == 200) {
            return response.body();

        } else {
            throw new HeadParsingException("A " + response.statusCode() + " error occurred while handling an HTTP request: " + response.body());
        }
    }
}