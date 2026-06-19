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

