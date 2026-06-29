package com.xiwanzi.otherworldinnhud.client.overlays;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.Location;
import com.xiwanzi.otherworldinnhud.config.DefaultValues.Client;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.otherworldinn.CurrentRoomInfo;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentFertility;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentSeason;
import com.xiwanzi.otherworldinnhud.impl.season.mods.CommonSeasonHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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
      Location hudLocation = OtherworldInnHudClient.getHudLocation();
      List<Component> lines = new ArrayList<>();
      lines.add(CurrentSeason.getInstance(mc).getHudText());

      CurrentFertility fertility = CurrentFertility.getInstance(mc);
      if (fertility.shouldDrawNewLine()) {
        lines.add(fertility.getHudText());
      }

      CurrentRoomInfo.getHudText(mc).ifPresent(roomHudText -> {
        lines.add(roomHudText.attributesLine());
        roomHudText.statusLine().ifPresent(lines::add);
      });

      int maxLineWidth = lines.stream().mapToInt(mc.font::width).max().orElse(0);
      int lineHeight = mc.font.lineHeight;
      int totalLineHeight = lineHeight * lines.size();
      int scaledWidth = (int) Math.ceil(maxLineWidth * scale);
      int scaledHeight = (int) Math.ceil(totalLineHeight * scale);
      int x;
      int y;

      switch (hudLocation) {
        case TOP_LEFT:
          x = defaultOffsetX;
          y = defaultOffsetY;
          break;

        case TOP_CENTER:
          x = (int) ((((double) screenWidth / 2) - ((double) scaledWidth / 2)) / scale);
          y = defaultOffsetY;
          break;

        case TOP_RIGHT:
          x = (int) ((screenWidth - scaledWidth - defaultOffsetX) / scale);
          y = defaultOffsetY;
          break;

        case BOTTOM_LEFT:
          x = defaultOffsetX;
          y = (int) ((screenHeight - scaledHeight - defaultOffsetY) / scale);
          break;

        case BOTTOM_RIGHT:
          x = (int) ((screenWidth - scaledWidth - defaultOffsetX) / scale);
          y = (int) ((screenHeight - scaledHeight - defaultOffsetY) / scale);
          break;

        case CUSTOM:
          x = offsetX;
          y = offsetY;
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + hudLocation);
      }

      // Text
      graphics.pose().pushPose();
      graphics.pose().scale((float) scale, (float) scale, 1F);
      int lineY = y;
      for (Component line : lines) {
        int lineX = getLineX(hudLocation, x, maxLineWidth, mc.font.width(line));
        graphics.drawString(mc.font, line, lineX, lineY, 0xffffff);
        lineY += lineHeight;
      }

      if (Services.PLATFORM.isDevelopmentEnvironment()) {
        CommonSeasonHelper.commonSeasons.getHelper().debugHud(graphics);
      }

      graphics.pose().popPose();
    }
  }

  private static int getLineX(Location hudLocation, int blockX, int maxLineWidth, int lineWidth) {
    return switch (hudLocation) {
      case TOP_CENTER -> blockX + ((maxLineWidth - lineWidth) / 2);
      case TOP_RIGHT, BOTTOM_RIGHT -> blockX + maxLineWidth - lineWidth;
      default -> blockX;
    };
  }
}
