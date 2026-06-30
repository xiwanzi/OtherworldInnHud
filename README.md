# Otherworld Inn HUD

A SeasonHud fork for the Otherworld Inn modpack.

Based on [IanMods/SeasonHud](https://github.com/IanMods/SeasonHud) `active/multi/1.21.1`.

Target:

* Minecraft `1.21.1`
* Java `21`
* NeoForge `21.1.x`

Only NeoForge is enabled for now. Fabric / Forge sources are kept from upstream.

## Changes

* Fix service loading on NeoForge / ModLauncher.
* Add room HUD for [Otherworld Inn](https://github.com/Yourzi/OtherworldInn).
* Add quest HUD for Otherworld Inn guests and town commissions.
* Keep upstream SeasonHud features and compatibility code.

## Otherworld Inn HUD

Room HUD shows when the player is inside a registered inn room:

* comfort
* light
* humidity
* cleanliness

Quest HUD shows while holding `Tab`:

* guest requests
* town commissions
* task progress
* rewards
* completion state

The integration is optional. If Otherworld Inn is missing or unavailable, these HUD elements are hidden.

## Config

Client config:

```text
config/otherworldinn_hud-client.toml
```

Server config:

```text
<world>/serverconfig/otherworldinn_hud-server.toml
```

## Build

```powershell
.\gradlew.bat :neoforge:build
```

Output:

```text
neoforge/build/libs/
```

## License

MIT, same as upstream SeasonHud.

See [LICENSE](LICENSE).
