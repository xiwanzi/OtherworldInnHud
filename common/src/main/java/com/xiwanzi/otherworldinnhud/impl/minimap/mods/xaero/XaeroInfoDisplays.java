package com.xiwanzi.otherworldinnhud.impl.minimap.mods.xaero;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.impl.minimap.CurrentMinimap;
import com.xiwanzi.otherworldinnhud.impl.minimap.mods.MinimapMods;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentFertility;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentSeason;
import net.minecraft.client.Minecraft;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.InfoDisplay.Builder;
import xaero.hud.minimap.info.widget.InfoDisplayCommonWidgetFactories;
import xaero.lib.common.config.option.value.io.serialization.BuiltInConfigValueIOCodecs;

public class XaeroInfoDisplays {
  public static final Builder<Boolean> SEASON_INFO_BUILDER;
  public static InfoDisplay<Boolean> SEASON;

  static {
    Minecraft mc = Minecraft.getInstance();

    Builder<Boolean> builder = Builder.begin();

    SEASON_INFO_BUILDER = builder.setId("season")
        .setName(Common.translatedText("xaerominimap.otherworldinn_hud.infodisplay.season"))
        .setDefaultState(true)
        .setCodec(BuiltInConfigValueIOCodecs.BOOLEAN)
        .setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON)
        .setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
          if (displayInfo.getEffectiveState() && CurrentMinimap.xaeroLoaded() && CurrentMinimap.shouldDrawMinimapHud(
              MinimapMods.XAERO, mc) && mc.level != null) {
            compiler.addLine(CurrentSeason.getInstance(mc).getHudText());

            if (CurrentFertility.getInstance(mc).shouldDrawNewLine()) {
              compiler.addLine(CurrentFertility.getInstance(mc).getMinimapText());
            }
          }
        });
  }
}