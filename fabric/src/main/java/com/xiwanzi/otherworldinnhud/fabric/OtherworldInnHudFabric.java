package com.xiwanzi.otherworldinnhud.fabric;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudServer;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.accessories.AccessoriesCompat;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class OtherworldInnHudFabric implements ModInitializer {

  public OtherworldInnHudFabric() {
  }

  /**
   * Runs the mod initializer.
   */
  @Override
  public void onInitialize() {
    Common.init();

    NeoForgeConfigRegistry.INSTANCE.register(Common.MOD_ID, ModConfig.Type.CLIENT, OtherworldInnHudClient.CLIENT_SPEC,
                                             "otherworldinn_hud-client.toml");

    NeoForgeConfigRegistry.INSTANCE.register(Common.MOD_ID, ModConfig.Type.SERVER, OtherworldInnHudServer.SERVER_SPEC,
                                             "otherworldinn_hud-server.toml");

    if (Common.accessoriesLoaded() && !Common.trinketsLoaded()) {
      AccessoriesCompat.init();
    }
  }
}