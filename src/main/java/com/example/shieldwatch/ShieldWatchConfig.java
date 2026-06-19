package com.example.shieldwatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain-old config object, serialized to config/shieldwatch.json.
 * Kept deliberately simple - no config-lib dependency, just Gson + a file.
 */
public class ShieldWatchConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir().resolve("shieldwatch.json");

	public boolean enabled = true;
	public boolean showPercent = false;
	public int warnThresholdPercent = 25;
	public boolean pulseWhenCritical = true;

	private static ShieldWatchConfig instance;

	public static ShieldWatchConfig get() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	public static ShieldWatchConfig load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
				ShieldWatchConfig loaded = GSON.fromJson(reader, ShieldWatchConfig.class);
				if (loaded != null) {
					return loaded;
				}
			} catch (IOException e) {
				ShieldWatchClient.LOGGER.warn("Failed to read ShieldWatch config, using defaults", e);
			}
		}
		ShieldWatchConfig fresh = new ShieldWatchConfig();
		fresh.save();
		return fresh;
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			ShieldWatchClient.LOGGER.warn("Failed to save ShieldWatch config", e);
		}
	}
}
