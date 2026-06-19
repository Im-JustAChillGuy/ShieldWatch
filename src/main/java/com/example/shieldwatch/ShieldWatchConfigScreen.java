package com.example.shieldwatch;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Plain vanilla-widget config screen - no extra GUI library dependency.
 * Each row is a button you click to cycle/toggle the value; "Done" saves
 * the config to disk and returns to the previous screen (Mod Menu's mod list).
 */
public class ShieldWatchConfigScreen extends Screen {

	private final Screen parent;
	private final ShieldWatchConfig config;

	private Button enabledButton;
	private Button percentButton;
	private Button pulseButton;
	private Button thresholdButton;

	protected ShieldWatchConfigScreen(Screen parent) {
		super(Component.translatable("shieldwatch.config.title"));
		this.parent = parent;
		this.config = ShieldWatchConfig.get();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int rowWidth = 200;
		int y = this.height / 2 - 60;
		int spacing = 24;

		enabledButton = Button.builder(enabledLabel(), b -> {
					config.enabled = !config.enabled;
					b.setMessage(enabledLabel());
				})
				.bounds(centerX - rowWidth / 2, y, rowWidth, 20)
				.build();
		this.addRenderableWidget(enabledButton);
		y += spacing;

		percentButton = Button.builder(percentLabel(), b -> {
					config.showPercent = !config.showPercent;
					b.setMessage(percentLabel());
				})
				.bounds(centerX - rowWidth / 2, y, rowWidth, 20)
				.build();
		this.addRenderableWidget(percentButton);
		y += spacing;

		pulseButton = Button.builder(pulseLabel(), b -> {
					config.pulseWhenCritical = !config.pulseWhenCritical;
					b.setMessage(pulseLabel());
				})
				.bounds(centerX - rowWidth / 2, y, rowWidth, 20)
				.build();
		this.addRenderableWidget(pulseButton);
		y += spacing;

		thresholdButton = Button.builder(thresholdLabel(), b -> {
					// cycle the warning threshold through a handful of presets
					int[] presets = {10, 15, 20, 25, 33, 50};
					int idx = 0;
					for (int i = 0; i < presets.length; i++) {
						if (presets[i] == config.warnThresholdPercent) {
							idx = i;
							break;
						}
					}
					config.warnThresholdPercent = presets[(idx + 1) % presets.length];
					b.setMessage(thresholdLabel());
				})
				.bounds(centerX - rowWidth / 2, y, rowWidth, 20)
				.build();
		this.addRenderableWidget(thresholdButton);
		y += spacing + 10;

		this.addRenderableWidget(
				Button.builder(Component.translatable("shieldwatch.config.done"), b -> onClose())
						.bounds(centerX - rowWidth / 2, y, rowWidth, 20)
						.build()
		);
	}

	private Component enabledLabel() {
		return Component.translatable("shieldwatch.config.enabled")
				.append(": " + (config.enabled ? "ON" : "OFF"));
	}

	private Component percentLabel() {
		return Component.translatable("shieldwatch.config.show_percent")
				.append(": " + (config.showPercent ? "ON" : "OFF"));
	}

	private Component pulseLabel() {
		return Component.translatable("shieldwatch.config.pulse")
				.append(": " + (config.pulseWhenCritical ? "ON" : "OFF"));
	}

	private Component thresholdLabel() {
		return Component.translatable("shieldwatch.config.warn_threshold")
				.append(": " + config.warnThresholdPercent + "%");
	}

	@Override
	public void onClose() {
		config.save();
		this.minecraft.setScreen(parent);
	}
}
