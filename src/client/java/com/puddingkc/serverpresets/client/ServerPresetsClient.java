package com.puddingkc.serverpresets.client;

import net.fabricmc.api.ClientModInitializer;

public class ServerPresetsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();
	}
}