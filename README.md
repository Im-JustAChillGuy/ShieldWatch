# ShieldWatch

A client-side Fabric mod for Minecraft **26.1.x** that overlays a live durability
countdown on your shield icon next to the hotbar, plus a Mod Menu config screen
to toggle the display, switch between a raw number and a percentage, set the
"low durability" warning threshold, and turn pulsing on/off.

## What's in here

```
shieldwatch/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── src/main/
    ├── java/com/example/shieldwatch/
    │   ├── ShieldWatchClient.java          # registers the HUD overlay
    │   ├── ShieldWatchConfig.java          # loads/saves config/shieldwatch.json
    │   ├── ShieldWatchConfigScreen.java    # plain vanilla-widget settings screen
    │   └── ShieldWatchModMenuIntegration.java
    └── resources/
        ├── fabric.mod.json
        └── assets/shieldwatch/lang/en_us.json
```

## Building it

You'll need **JDK 25** (26.1 requires it) and an internet connection so Gradle
can pull Minecraft, Fabric Loader, Fabric API, and Mod Menu.

```bash
./gradlew build
```

The Gradle wrapper jar itself isn't included in this zip (binary files don't
travel well through chat) - run `gradle wrapper --gradle-version 9.4` once
inside the folder first (any recent Gradle with the wrapper plugin works),
or just open the folder in IntelliJ IDEA 2025.3+ and let it generate the
wrapper for you. The built mod jar will show up in `build/libs/`.

Drop the resulting jar, plus **Fabric API** and **Mod Menu** for 26.1.x, into
your `mods` folder.

## Versions this targets

Minecraft re-versioned itself in early 2026 to a `year.drop.hotfix` scheme,
and 26.1 is the first fully unobfuscated release built on Mojang's official
mappings instead of Yarn. That's a bigger jump than a normal version bump:

- `minecraft_version=26.1.2`
- `loader_version=0.18.5`
- `loom_version=1.15.5` (the new `net.fabricmc.fabric-loom` plugin, no remapping step)
- `fabric_api_version=0.144.3+26.1`
- `modmenu_version=18.0.0-beta.1`
- Java 25 (set in `build.gradle`)

These were accurate as of late June 2026. **Check
[fabricmc.net/develop](https://fabricmc.net/develop) before you build** -
26.1 is new enough that patch versions of Loader/Loom/Fabric API are still
shipping frequently, and a stale version number is the #1 reason a fresh
Fabric project fails to resolve dependencies.

## Heads up on the HUD code specifically

The HUD rendering pipeline (`HudElementRegistry`, `GuiGraphicsExtractor`,
`VanillaHudElements`) was substantially rewritten for 26.1 - it's genuinely
new API, not just a rename. I wrote `ShieldWatchClient.java` against
Fabric's current official docs for it, but two things are worth knowing:

1. **Pixel alignment**: vanilla's off-hand icon position is computed from
   `OFFSET_X` / `OFFSET_Y` constants at the top of `ShieldWatchClient.java`.
   If the number doesn't land exactly on your shield icon (different GUI
   scale, resolution, or a future tiny layout tweak), just nudge those two
   constants - that's the one part of vanilla layout I approximated rather
   than read straight out of the game's source.
2. If something doesn't compile because a method got renamed again between
   26.1 hotfixes, the fastest fix is the live docs:
   [docs.fabricmc.net/develop/rendering/hud](https://docs.fabricmc.net/develop/rendering/hud)
   and
   [docs.fabricmc.net/develop/rendering/gui-graphics](https://docs.fabricmc.net/develop/rendering/gui-graphics).

## Extending it

- The repair-material hint, durability-loss-rate display, and multi-shield
  comparison ideas we talked about would all hang off the same
  `renderOverlay` method in `ShieldWatchClient` - you've already got the
  `ItemStack` for the shield in hand right there.
- Want it to also watch your main-hand tool/weapon durability the same way?
  `findShield()` is the only method that's shield-specific; everything else
  is generic to any damageable `ItemStack`.
