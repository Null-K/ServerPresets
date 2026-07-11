package com.puddingkc.serverpresets.client;

public class PresetServer {
    private final String name;
    private final String ip;
    private String resourcePackPolicy; // ENABLED, DISABLED, PROMPT

    public PresetServer(String name, String ip) {
        this.name = name;
        this.ip = ip;
        this.resourcePackPolicy = "PROMPT";
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getResourcePackPolicy() {
        return resourcePackPolicy;
    }

    public void setResourcePackPolicy(String resourcePackPolicy) {
        this.resourcePackPolicy = resourcePackPolicy;
    }
}
