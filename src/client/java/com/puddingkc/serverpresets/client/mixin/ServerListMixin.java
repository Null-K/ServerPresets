package com.puddingkc.serverpresets.client.mixin;

import com.puddingkc.serverpresets.client.ConfigManager;
import com.puddingkc.serverpresets.client.PresetServer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerList.class)
public abstract class ServerListMixin {
    @Shadow
    @Final
    private List<ServerData> serverList;

    @Shadow
    public abstract ServerData get(int index);

    @Unique
    private int presetServerCount = 0;

    @Unique
    private boolean isPresetServer(ServerData serverData) {
        if (serverData == null) {
            return false;
        }
        List<PresetServer> presetServers = ConfigManager.getConfig().getPresetServers();
        for (PresetServer preset : presetServers) {
            if (preset.getIp().equals(serverData.ip)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private ServerData.ServerPackStatus getResourcePackPolicy(String policy) {
        if (policy == null) {
            return ServerData.ServerPackStatus.PROMPT;
        }
        return switch (policy.toUpperCase()) {
            case "ENABLED" -> ServerData.ServerPackStatus.ENABLED;
            case "DISABLED" -> ServerData.ServerPackStatus.DISABLED;
            default -> ServerData.ServerPackStatus.PROMPT;
        };
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void onLoad(CallbackInfo ci) {
        List<PresetServer> presetServers = ConfigManager.getConfig().getPresetServers();

        // 移除已存在的重复服务器
        for (int i = serverList.size() - 1; i >= 0; i--) {
            ServerData existing = serverList.get(i);
            for (PresetServer preset : presetServers) {
                if (existing.ip.equals(preset.getIp())) {
                    serverList.remove(i);
                    break;
                }
            }
        }

        // 从后往前插入预设服务器到顶部
        for (int i = presetServers.size() - 1; i >= 0; i--) {
            PresetServer preset = presetServers.get(i);
            ServerData serverData = new ServerData(preset.getName(), preset.getIp(), ServerData.Type.OTHER);

            serverData.setResourcePackStatus(getResourcePackPolicy(preset.getResourcePackPolicy()));

            serverList.addFirst(serverData);
        }

        presetServerCount = presetServers.size();
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void onRemove(ServerData thing, CallbackInfo ci) {
        if (isPresetServer(thing)) {
            ci.cancel();
        }
    }

    @Inject(method = "replace", at = @At("HEAD"), cancellable = true)
    private void onReplace(int id, ServerData data, CallbackInfo ci) {
        if (id < presetServerCount) {
            ci.cancel();
        }
    }

    @Inject(method = "swap", at = @At("HEAD"), cancellable = true)
    private void onSwap(int a, int b, CallbackInfo ci) {
        if (a < presetServerCount || b < presetServerCount) {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void onSaveBegin(CallbackInfo ci) {
        List<PresetServer> presetServers = ConfigManager.getConfig().getPresetServers();

        // 移除列表中错误位置的预设服务器
        for (int i = serverList.size() - 1; i >= presetServerCount; i--) {
            ServerData server = serverList.get(i);
            if (isPresetServer(server)) {
                serverList.remove(i);
            }
        }

        // 确保预设服务器按正确顺序在顶部
        int insertPosition = 0;
        for (PresetServer preset : presetServers) {
            boolean found = false;

            for (int i = 0; i < serverList.size(); i++) {
                ServerData server = serverList.get(i);
                if (server.ip.equals(preset.getIp())) {
                    if (i != insertPosition) {
                        serverList.remove(i);
                        serverList.add(insertPosition, server);
                    }

                    server.setResourcePackStatus(getResourcePackPolicy(preset.getResourcePackPolicy()));
                    found = true;
                    break;
                }
            }

            if (!found) {
                ServerData serverData = new ServerData(preset.getName(), preset.getIp(), ServerData.Type.OTHER);
                serverData.setResourcePackStatus(getResourcePackPolicy(preset.getResourcePackPolicy()));
                serverList.add(insertPosition, serverData);
            }

            insertPosition++;
        }

        presetServerCount = presetServers.size();
    }
}
