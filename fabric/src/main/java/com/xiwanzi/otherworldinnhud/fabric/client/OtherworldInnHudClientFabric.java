package com.xiwanzi.otherworldinnhud.fabric.client;

import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import com.xiwanzi.otherworldinnhud.fabric.event.ClientEvents;
import net.fabricmc.api.ClientModInitializer;

public class OtherworldInnHudClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientEvents.register();

    OtherworldInnHudClientCommon.initAccessoriesClient();
    OtherworldInnHudClientCommon.ftbChunkSetup();
  }
}
