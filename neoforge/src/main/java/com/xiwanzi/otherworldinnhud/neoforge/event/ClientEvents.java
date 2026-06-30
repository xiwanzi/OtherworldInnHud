package com.xiwanzi.otherworldinnhud.neoforge.event;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.neoforge.client.overlays.OtherworldInnHudOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ClientEvents {
  private ClientEvents() {
  }

  @EventBusSubscriber(value = Dist.CLIENT, modid = Common.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
  public static class ModBus {
    private ModBus() {
    }

    // Overlays
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
      OtherworldInnHudOverlay.init();
      event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, Common.location("season_hud"),
                          OtherworldInnHudOverlay.HUD_INSTANCE);
    }
  }
}
