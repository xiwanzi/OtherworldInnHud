package com.xiwanzi.otherworldinnhud.client;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.accessories.AccessoriesCompat;
import com.xiwanzi.otherworldinnhud.impl.minimap.mods.ftbchunks.SeasonComponent;

public class OtherworldInnHudClientCommon {
  public static void initAccessoriesClient() {
    if (Common.accessoriesLoaded() && Calendar.calendar().isPresent() && !Common.curiosLoaded()) {
      AccessoriesCompat.clientInit();
    }
  }

  public static void ftbChunkSetup() {
    if (Common.ftbChunksLoaded()) {
      SeasonComponent.ftbChunkSetup();
    }
  }
}
