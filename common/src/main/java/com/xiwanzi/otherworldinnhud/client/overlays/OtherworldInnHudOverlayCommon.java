package com.xiwanzi.otherworldinnhud.client.overlays;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.config.DefaultValues.Client;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentFertility;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentSeason;
import com.xiwanzi.otherworldinnhud.impl.season.mods.CommonSeasonHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class OtherworldInnHudOverlayCommon {
  private OtherworldInnHudOverlayCommon() {
  }

  public static void render(@NonNull GuiGraphics graphics, @NonNull DeltaTracker tickCounter) {
    Minecraft mc = Minecraft.getInstance();

    if (Common.drawDefaultHud(mc) && Common.vanillaShouldDrawHud(mc) && Calendar.validNeedCalendar(mc.player)
        && !Common.hideHudInCurrentDimension(mc)) {
      int screenWidth = mc.getWindow().getGuiScaledWidth();
      int screenHeight = mc.getWindow().getGuiScaledHeight();
      int offsetX = OtherworldInnHudClient.getHudX();
      int offsetY = OtherworldInnHudClient.getHudY();
      double scale = OtherworldInnHudClient.getHudScale();
      int defaultOffsetX = Client.DEFAULT_X_OFFSET;
      int defaultOffsetY = Client.DEFAULT_Y_OFFSET;
      MutableComponent seasonCombined = CurrentSeason.getInstance(mc).getHudText();
      int stringWidth = (int) (mc.font.width(seasonCombined) * scale);
      int stringHeight = (int) (mc.font.lineHeight * scale);
      int x;
      int y;

      switch (OtherworldInnHudClient.getHudLocation()) {
        case TOP_LEFT:
          x = defaultOffsetX;
          y = defaultOffsetY;
          break;

        case TOP_CENTER:
          x = (int) ((((double) screenWidth / 2) - ((double) stringWidth / 2)) / scale);
          y = defaultOffsetY;
          break;

        case TOP_RIGHT:
          x = (int) ((screenWidth - stringWidth - defaultOffsetX) / scale);
          y = defaultOffsetY;
          break;

        case BOTTOM_LEFT:
          x = defaultOffsetX;
          y = (int) (((screenHeight - stringHeight - defaultOffsetY)) / scale);
          break;

        case BOTTOM_RIGHT:
          x = (int) (((screenWidth - stringWidth - defaultOffsetX)) / scale);
          y = (int) (((screenHeight - stringHeight - defaultOffsetY)) / scale);
          break;

        case CUSTOM:
          x = offsetX;
          y = offsetY;
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + OtherworldInnHudClient.getHudLocation());
      }

      // Text
      graphics.pose().pushPose();
      graphics.pose().scale((float) scale, (float) scale, 1F);
      graphics.drawString(mc.font, seasonCombined, x, y, 0xffffff);
      if (CurrentFertility.getInstance(mc).shouldDrawNewLine()) {
        MutableComponent fertility = CurrentFertility.getInstance(mc).getHudText();

        y += stringHeight;
        graphics.drawString(mc.font, fertility, x, y, 0xffffff);
      }

      if (Services.PLATFORM.isDevelopmentEnvironment()) {
        CommonSeasonHelper.commonSeasons.getHelper().debugHud(graphics);
      }

      graphics.pose().popPose();
    }
  }
}
