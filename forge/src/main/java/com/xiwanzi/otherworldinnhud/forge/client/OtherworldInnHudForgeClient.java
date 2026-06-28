package com.xiwanzi.otherworldinnhud.forge.client;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Common.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OtherworldInnHudForgeClient {

  @SubscribeEvent
  public static void onInitializeClient(FMLClientSetupEvent event) {
    OtherworldInnHudClientCommon.initAccessoriesClient();
    OtherworldInnHudClientCommon.ftbChunkSetup();
  }
}
