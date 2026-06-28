package com.xiwanzi.otherworldinnhud.forge.client.overlays;

import com.xiwanzi.otherworldinnhud.client.overlays.OtherworldInnHudOverlayCommon;
import javax.annotation.Nonnull;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class OtherworldInnHudOverlay implements LayeredDraw.Layer {
  public static OtherworldInnHudOverlay HUD_INSTANCE;

  public static void init() {
    HUD_INSTANCE = new OtherworldInnHudOverlay();
  }

  @Override
  public void render(@Nonnull GuiGraphics graphics, @Nonnull DeltaTracker tickCounter) {
    OtherworldInnHudOverlayCommon.render(graphics, tickCounter);
  }
}
