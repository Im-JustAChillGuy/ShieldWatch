package com.example.shieldwatch;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ShieldWatch - client-side only mod.
 * Draws a small durability readout over the shield icon next to the hotbar
 * (the slot vanilla uses for your off-hand item when it's a shield/usable item).
 *
 * NOTE: 26.1 just rewrote the HUD rendering pipeline around HudElementRegistry /
 * GuiGraphicsExtractor. The exact pixel offsets vanilla uses for the off-hand
 * icon can shift slightly between drops - if the overlay doesn't line up with
 * your shield icon perfectly, nudge OFFSET_X / OFFSET_Y below, or expose them
 * in the config screen.
 */
public class ShieldWatchClient implements ClientModInitializer {

	public static final String MOD_ID = "shieldwatch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Tweak these if the overlay doesn't sit exactly on top of the shield icon
	// for your GUI scale / resolution.
	private static final int OFFSET_X = 2;
	private static final int OFFSET_Y = -10;

	@Override
	public void onInitializeClient() {
		ShieldWatchConfig.get(); // warm the config / write defaults on first run

		HudElementRegistry.attachElementAfter(
				VanillaHudElements.HOTBAR,
				Identifier.fromNamespaceAndPath(MOD_ID, "shield_durability_overlay"),
				ShieldWatchClient::renderOverlay
		);
	}

	private static void renderOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		ShieldWatchConfig config = ShieldWatchConfig.get();
		if (!config.enabled) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null) {
			return;
		}

		ItemStack shieldStack = findShield(player);
		if (shieldStack == null || shieldStack.isEmpty()) {
			return;
		}

		if (!shieldStack.isDamageableItem()) {
			return;
		}

		int maxDamage = shieldStack.getMaxDamage();
		int damage = shieldStack.getDamageValue();
		int remaining = Math.max(0, maxDamage - damage);
		float remainingFraction = maxDamage > 0 ? (float) remaining / (float) maxDamage : 1.0f;
		int percent = Math.round(remainingFraction * 100f);

		Window window = client.getWindow();
		int screenWidth = window.getGuiScaledWidth();
		int screenHeight = window.getGuiScaledHeight();

		// Vanilla draws the off-hand icon just to the left of the centered hotbar,
		// roughly at (center - 91 - 29, height - 23). We anchor our text just
		// above/inside that icon.
		int hotbarHalfWidth = 91;
		int offhandIconX = (screenWidth / 2) - hotbarHalfWidth - 29;
		int offhandIconY = screenHeight - 23;

		int x = offhandIconX + OFFSET_X;
		int y = offhandIconY + OFFSET_Y;

		int color = colorFor(remainingFraction, config.warnThresholdPercent);

		if (config.pulseWhenCritical && percent <= config.warnThresholdPercent) {
			float pulse = (Mth.sin((float) (System.currentTimeMillis() % 1000L) / 1000f * (float) Math.PI * 2f) + 1f) / 2f;
			int dim = (color & 0x00FFFFFF) | 0x60000000;
			int bright = (color & 0x00FFFFFF) | 0xFF000000;
			color = ARGB.linearLerp(pulse, dim, bright);
		}

		String label = config.showPercent ? (percent + "%") : String.valueOf(remaining);

		// center the label roughly under/over the 16px item icon
		int textWidth = client.font.width(label);
		int centeredX = x + (16 - textWidth) / 2;

		graphics.text(client.font, label, centeredX, y, color, true);
	}

	private static ItemStack findShield(LocalPlayer player) {
		ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (offhand.getItem() == Items.SHIELD) {
			return offhand;
		}
		ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (mainhand.getItem() == Items.SHIELD) {
			return mainhand;
		}
		return null;
	}

	private static int colorFor(float remainingFraction, int warnThresholdPercent) {
		float warnFraction = warnThresholdPercent / 100f;
		int red, green;
		if (remainingFraction <= warnFraction) {
			// red -> yellow as it approaches the warn threshold from below
			float t = warnFraction <= 0 ? 0 : Mth.clamp(remainingFraction / Math.max(warnFraction, 0.0001f), 0f, 1f);
			red = 255;
			green = Math.round(t * 255f);
		} else {
			// yellow -> green above the warn threshold
			float t = Mth.clamp((remainingFraction - warnFraction) / Math.max(1f - warnFraction, 0.0001f), 0f, 1f);
			red = Math.round((1f - t) * 255f);
			green = 255;
		}
		return 0xFF000000 | (red << 16) | (green << 8);
	}
}
