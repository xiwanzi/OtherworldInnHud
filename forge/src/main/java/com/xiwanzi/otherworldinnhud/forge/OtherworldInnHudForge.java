package com.xiwanzi.otherworldinnhud.forge;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudServer;
import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Common.MOD_ID)
public class OtherworldInnHudForge {
  public OtherworldInnHudForge(FMLJavaModLoadingContext context) {
    Common.init();
    MinecraftForge.EVENT_BUS.register(this);
    var modEventBus = context.getModEventBus();

    NeoForgeConfigRegistry.INSTANCE.register(Common.MOD_ID, ModConfig.Type.CLIENT, OtherworldInnHudClient.CLIENT_SPEC,
                                             "otherworldinn_hud-client.toml");

    NeoForgeConfigRegistry.INSTANCE.register(Common.MOD_ID, ModConfig.Type.SERVER, OtherworldInnHudServer.SERVER_SPEC,
                                             "otherworldinn_hud-server.toml");

    modEventBus.addListener(OtherworldInnHudForge::onInitialize);

  }

  public static void onInitialize(FMLCommonSetupEvent event) {
    // if (Common.curiosLoaded()) {
    //   Common.LOG.info("Talking to Curios");
    //   CuriosCompat.init();
    // } else if (Common.accessoriesLoaded()) {
    //   AccessoriesCompat.init();
    // }
  }
}