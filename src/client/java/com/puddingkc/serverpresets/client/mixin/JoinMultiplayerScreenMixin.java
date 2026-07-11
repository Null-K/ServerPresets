package com.puddingkc.serverpresets.client.mixin;

import com.puddingkc.serverpresets.client.ConfigManager;
import com.puddingkc.serverpresets.client.PresetServer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin {
    @Shadow
    private ServerList servers;

    @Shadow
    private Button editButton;

    @Shadow
    private Button deleteButton;

    @Shadow
    protected ServerSelectionList serverSelectionList;

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

    @Inject(method = "onSelectedChange", at = @At("RETURN"))
    private void onSelectedChangeAfter(CallbackInfo ci) {
        ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
        if (entry instanceof ServerSelectionList.OnlineServerEntry) {
            ServerData serverData = ((ServerSelectionList.OnlineServerEntry)entry).getServerData();
            if (isPresetServer(serverData)) {
                this.editButton.active = false;
                this.deleteButton.active = false;
            }
        }
    }

    @Inject(method = "deleteCallback", at = @At("HEAD"), cancellable = true)
    private void onDeleteCallback(boolean result, CallbackInfo ci) {
        if (result) {
            ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
            if (entry instanceof ServerSelectionList.OnlineServerEntry) {
                ServerData serverData = ((ServerSelectionList.OnlineServerEntry)entry).getServerData();
                if (isPresetServer(serverData)) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "editServerCallback", at = @At("HEAD"), cancellable = true)
    private void onEditServerCallback(boolean result, CallbackInfo ci) {
        if (result) {
            ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
            if (entry instanceof ServerSelectionList.OnlineServerEntry) {
                ServerData serverData = ((ServerSelectionList.OnlineServerEntry)entry).getServerData();
                if (isPresetServer(serverData)) {
                    ci.cancel();
                }
            }
        }
    }
}
