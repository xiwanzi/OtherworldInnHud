package com.xiwanzi.otherworldinnhud.fabric.client.overlays;

import com.xiwanzi.otherworldinnhud.client.overlays.OtherworldInnHudOverlayCommon;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class OtherworldInnHudOverlay implements HudRenderCallback {
  public static OtherworldInnHudOverlay HUD_INSTANCE;

  public static void init() {
    HUD_INSTANCE = new OtherworldInnHudOverlay();
    HudRenderCallback.EVENT.register(HUD_INSTANCE);
  }

  @Override
  public void onHudRender(GuiGraphics graphics, DeltaTracker tickCounter) {
    OtherworldInnHudOverlayCommon.render(graphics, tickCounter);
  }
}