package com.xiwanzi.otherworldinnhud.neoforge.event;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.KeyBindings;
import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import com.xiwanzi.otherworldinnhud.neoforge.client.overlays.OtherworldInnHudOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = Common.MOD_ID)
public class ClientEvents {

  @SubscribeEvent
  public static void onKeyInput(InputEvent.Key event) {
    OtherworldInnHudClientCommon.optionsKeyInput();
  }

  @EventBusSubscriber(value = Dist.CLIENT, modid = Common.MOD_ID)
  public static class ModBus {
    // Overlays
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
      OtherworldInnHudOverlay.init();
      event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, Common.location("season_hud"),
                          OtherworldInnHudOverlay.HUD_INSTANCE);
    }

    // Key Bindings
    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
      event.register(KeyBindings.otherworldInnHudOptionsKeyMapping);
    }
  }
}
