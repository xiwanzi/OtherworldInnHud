package com.xiwanzi.otherworldinnhud.neoforge.impl.curios;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.neoforge.impl.curios.item.CuriosCalendar;

public class CuriosCompat {
  public CuriosCompat() {
  }

  public static void init() {
    if (Common.curiosLoaded() && Common.hasCalendarLoaded()) {
      CuriosCalendar.init();
    }
  }
}
