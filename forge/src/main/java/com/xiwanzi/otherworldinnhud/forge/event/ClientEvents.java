package com.xiwanzi.otherworldinnhud.forge.event;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.KeyBindings;
import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import com.xiwanzi.otherworldinnhud.forge.client.overlays.OtherworldInnHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

  @Mod.EventBusSubscriber(modid = Common.MOD_ID, value = Dist.CLIENT)
  public static class ClientForgeEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
      OtherworldInnHudClientCommon.optionsKeyInput();
    }
  }

  @Mod.EventBusSubscriber(modid = Common.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientModBusEvents {
    // Overlays
    @SubscribeEvent
    public static void registerGuiOverlays(AddGuiOverlayLayersEvent event) {
      OtherworldInnHudOverlay.init();

      ForgeLayeredDraw layeredDraw = event.getLayeredDraw();
      layeredDraw.addAbove(ForgeLayeredDraw.PRE_SLEEP_STACK, Common.location("season_hud"),
                           ForgeLayeredDraw.CAMERA_OVERLAY, OtherworldInnHudOverlay.HUD_INSTANCE);
    }

    // Key Bindings
    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
      event.register(KeyBindings.otherworldInnHudOptionsKeyMapping);
    }
  }
}
