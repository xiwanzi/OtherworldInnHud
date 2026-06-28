# Otherworld Inn HUD

Otherworld Inn HUD is a refactored build of SeasonHud for the Otherworld Inn
modpack.

This repository is initialized from IanMods/SeasonHud `active/multi/1.21.1`
at commit `f0242530dab1057e6f46073ca5d4a5da8527324c`.

## Current Scope

- Use `otherworldinn_hud` as the mod id.
- Use `Otherworld Inn HUD` as the display name.
- Publish package metadata under author `xiwanzi`.
- Build NeoForge by default. The upstream Fabric and Forge source trees are kept
  for reference, but `enabled_platforms` is set to `neoforge` for this modpack.
- Preserve the existing season HUD, config, minimap, and accessory behavior.
- Patch service discovery to avoid NeoForge/ModLauncher context classloader
  instability during parallel client setup.
- Add a GitHub Actions build workflow for every push and pull request.

## Patch

`Services.load` uses:

```java
ServiceLoader.load(clazz, clazz.getClassLoader())
```

instead of relying on the current thread context classloader.

## License

SeasonHud is MIT licensed. The original copyright and MIT license text are
retained in `LICENSE`.
