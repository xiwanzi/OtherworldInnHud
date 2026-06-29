package com.xiwanzi.otherworldinnhud.neoforge;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.screens.MainOptionsScreen;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudServer;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.accessories.AccessoriesCompat;
import com.xiwanzi.otherworldinnhud.neoforge.impl.curios.CuriosCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Common.MOD_ID)
public class OtherworldInnHudNeoForge {

  public OtherworldInnHudNeoForge(IEventBus modEventBus, ModContainer modContainer) {
    modContainer.registerConfig(ModConfig.Type.CLIENT, OtherworldInnHudClient.CLIENT_SPEC, "otherworldinn_hud-client.toml");
    modContainer.registerConfig(ModConfig.Type.SERVER, OtherworldInnHudServer.SERVER_SPEC, "otherworldinn_hud-server.toml");
    if (FMLEnvironment.dist == Dist.CLIENT) {
      modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                                          (container, parent) -> MainOptionsScreen.getInstance(parent));
    }

    modEventBus.addListener(OtherworldInnHudNeoForge::onInitialize);
  }

  public static void onInitialize(FMLCommonSetupEvent event) {
    if (Common.curiosLoaded()) {
      Common.LOG.info("Talking to Curios");
      CuriosCompat.init();
    } else if (Common.accessoriesLoaded()) {
      AccessoriesCompat.init();
    }
  }
}
