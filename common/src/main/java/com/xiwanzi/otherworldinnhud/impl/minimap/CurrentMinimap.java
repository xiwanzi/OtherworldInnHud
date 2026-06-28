package com.xiwanzi.otherworldinnhud.impl.minimap;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.minimap.mods.MinimapMods;
import com.xiwanzi.otherworldinnhud.platform.Services;
import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class CurrentMinimap {
  public CurrentMinimap() {
  }

  public static boolean xaeroLoaded() {
    return MinimapMods.XAERO.modLoaded() || MinimapMods.XAERO_FAIRPLAY.modLoaded()
        || MinimapMods.XAERO_BETTER_PVP.modLoaded();
  }

  public static boolean journeyMapLoaded() {
    return MinimapMods.JOURNEYMAP.modLoaded();
  }

  public static boolean ftbChunksLoaded() {
    return MinimapMods.FTB_CHUNKS.modLoaded();
  }

  public static boolean mapAtlasesLoaded() {
    return MinimapMods.MAP_ATLASES.modLoaded();
  }

  public static boolean noMinimapLoaded() {
    return MinimapMods.getLoaded().isEmpty();
  }

  /* TODO:
   ** Double check all logic
   ** Add option to display current loaded integration
   ** Add a dropdown to override this if more than one are loaded?
   */

  /**
   * Finds if the minimap is hidden.
   *
   * @param minimap Current loaded minimap mod
   * @return True if the minimap is not currently displayed
   */
  public static boolean hiddenMinimap(MinimapMods minimap, Minecraft mc) {
    switch (minimap) {
      case JOURNEYMAP -> {
        return Services.MINIMAP.hideJourneyMap(mc);
      }
      case FTB_CHUNKS -> {
        return !FTBChunksClientConfig.MINIMAP_ENABLED.get() || mc.getDebugOverlay().showDebugScreen();
      }
      case XAERO, XAERO_FAIRPLAY, XAERO_BETTER_PVP -> {
        return Services.MINIMAP.hideXaero(mc);
      }
      case MAP_ATLASES -> {
        return Services.MINIMAP.hideMapAtlases(mc);
      }
      default -> {
        return false;
      }
    }
  }

  /**
   * Used in case FtbChunks or Journeymap are loaded, but not used for the minimap.
   *
   * @return True if all the loaded minimap are hidden.
   */
  public static boolean allMinimapsHidden(Minecraft mc) {
    List<Boolean> hiddenMinimaps = new ArrayList<>();

    MinimapMods.getLoaded().forEach(minimap -> hiddenMinimaps.add(hiddenMinimap(minimap, mc)));

    return Common.allTrue(hiddenMinimaps);
  }

  /**
   * Determines if the season hud should be drawn.
   *
   * @param minimap Current loaded minimap mod
   * @return True if the minimap version of the HUD should be drawn instead of the default
   */
  public static boolean shouldDrawMinimapHud(MinimapMods minimap, Minecraft mc) {
    if (mc.level == null || mc.player == null) {
      return false;
    }

    boolean enabled = OtherworldInnHudClient.getEnableMod() && OtherworldInnHudClient.getEnableMinimapIntegration();
    boolean hiddenMinimap = Common.hideHudInCurrentDimension(mc) || hiddenMinimap(minimap, mc);

    return enabled && Calendar.validNeedCalendar(mc.player) && !mc.options.hideGui && !hiddenMinimap;
  }
}