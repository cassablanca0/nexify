package net.nexify;

import net.fabricmc.api.ClientModInitializer;

public class NexifyClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		System.out.println("[Nexify] Client initialized.");
	}
}