package com.puddingkc.serverpresets.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ServerListLoader {
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static List<PresetServer> loadFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            System.out.println("Loading server list from URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                ServerListData data = GSON.fromJson(json, ServerListData.class);

                if (data != null && data.servers != null) {
                    System.out.println("Successfully loaded " + data.servers.size() + " servers from URL");
                    return data.servers;
                } else {
                    System.err.println("Invalid JSON format from URL");
                }
            } else {
                System.err.println("Failed to load from URL, HTTP status: " + response.statusCode());
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format from URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Network error loading from URL: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Request interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Unexpected error loading from URL: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    private static class ServerListData {
        List<PresetServer> servers;
    }
}
