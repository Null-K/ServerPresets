package com.puddingkc.serverpresets.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("serverpresets.json");
    private static Config config;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, Config.class);
            } catch (IOException e) {
                System.err.println("Failed to load server presets config: " + e.getMessage());
                config = createDefaultConfig();
            }
        } else {
            config = createDefaultConfig();
            save();
        }

        // 从 URL 加载服务器列表
        if (config.isEnableRemote() && config.getRemoteUrl() != null && !config.getRemoteUrl().isEmpty()) {
            List<PresetServer> remoteServers = ServerListLoader.loadFromUrl(config.getRemoteUrl());
            if (!remoteServers.isEmpty()) {
                // 合并远程服务器列表和本地列表
                List<PresetServer> allServers = new ArrayList<>(config.getPresetServers());
                allServers.addAll(remoteServers);
                config.setPresetServers(allServers);
                System.out.println("Merged local and remote server lists. Total: " + allServers.size());
            }
        }
    }

    public static void save() {
        try {
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            System.err.println("Failed to save server presets config: " + e.getMessage());
        }
    }

    private static Config createDefaultConfig() {
        Config config = new Config();
        List<PresetServer> servers = new ArrayList<>();
        PresetServer example = new PresetServer("Example Server", "mc.example.com");
        example.setResourcePackPolicy("PROMPT");
        servers.add(example);
        config.setPresetServers(servers);
        return config;
    }

    public static Config getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }

    public static class Config {
        private List<PresetServer> presetServers = new ArrayList<>();
        private String remoteUrl = "";
        private boolean enableRemote = false;

        public List<PresetServer> getPresetServers() {
            return presetServers;
        }

        public void setPresetServers(List<PresetServer> presetServers) {
            this.presetServers = presetServers;
        }

        public String getRemoteUrl() {
            return remoteUrl;
        }

        public boolean isEnableRemote() {
            return enableRemote;
        }
    }
}
