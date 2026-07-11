package com.puddingkc.serverpresets.client.mixin;

import com.puddingkc.serverpresets.client.ConfigManager;
import com.puddingkc.serverpresets.client.PresetServer;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class ServerSelectionListEntryMixin {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract ServerData getServerData();

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
    private int getPresetServerCount() {
        return ConfigManager.getConfig().getPresetServers().size();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        ServerSelectionList.OnlineServerEntry self = (ServerSelectionList.OnlineServerEntry)(Object)this;
        int relX = (int)event.x() - self.getContentX();
        int relY = (int)event.y() - self.getContentY();

        boolean clickedMoveUp = mouseOverTopLeftQuarter(relX, relY);
        boolean clickedMoveDown = mouseOverBottomLeftQuarter(relX, relY);

        if (isPresetServer(getServerData())) {
            if (clickedMoveUp || clickedMoveDown) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (clickedMoveUp) {
            int currentIndex = this.screen.getServers().size();
            for (int i = 0; i < this.screen.getServers().size(); i++) {
                if (this.screen.getServers().get(i).ip.equals(getServerData().ip)) {
                    currentIndex = i;
                    break;
                }
            }

            if (currentIndex - 1 < getPresetServerCount()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private boolean mouseOverTopLeftQuarter(int relX, int relY) {
        return relX < 32 / 2 && relY < 32 / 2;
    }

    @Unique
    private boolean mouseOverBottomLeftQuarter(int relX, int relY) {
        return relX < 32 / 2 && relY > 32 / 2;
    }
}