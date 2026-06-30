package com.xiwanzi.otherworldinnhud.neoforge.client;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Common.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class OtherworldInnHudNeoForgeClient {

  @SubscribeEvent
  public static void onInitializeClient(FMLClientSetupEvent event) {
    Common.init();
    OtherworldInnHudClientCommon.initAccessoriesClient();
    OtherworldInnHudClientCommon.ftbChunkSetup();
  }
}
