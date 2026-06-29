package com.xiwanzi.otherworldinnhud.mixin;

import com.xiwanzi.otherworldinnhud.client.overlays.TaskHudOverlayCommon;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiCrosshairMixin {
  @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
  private void otherworldinnHud$hideCrosshairForTaskHud(
      GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    if (TaskHudOverlayCommon.shouldHideCrosshair(Minecraft.getInstance())) {
      ci.cancel();
    }
  }
}
