package com.xiwanzi.otherworldinnhud.impl.accessory.mods.accessories;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.accessories.item.AccessoriesCalendar;
import com.xiwanzi.otherworldinnhud.platform.Services;

public class AccessoriesCompat {
  public AccessoriesCompat() {
  }

  public static void clientInit() {
    if (Common.accessoriesLoaded() && Common.hasCalendarLoaded()) {
      Common.LOG.info("Talking to Accessories Client");
      AccessoriesCalendar.clientInit();
    }
  }

  public static void init() {
    if (Common.accessoriesLoaded() && Common.hasCalendarLoaded()) {
      Common.LOG.info("Talking to Accessories {}", Services.PLATFORM.getModVersion("accessories"));
      AccessoriesCalendar.init();
    }
  }
}