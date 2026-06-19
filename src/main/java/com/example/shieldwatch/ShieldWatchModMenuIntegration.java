package com.example.shieldwatch;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Picked up automatically because of the "modmenu" entrypoint declared in
 * fabric.mod.json - Mod Menu calls getModConfigScreenFactory() when the user
 * clicks the gear icon next to ShieldWatch in the mods list.
 */
public class ShieldWatchModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ShieldWatchConfigScreen::new;
	}
}
