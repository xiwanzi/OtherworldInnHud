package com.xiwanzi.otherworldinnhud.neoforge.impl.curios.item;

import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosCalendar implements ICurioItem {
  public CuriosCalendar() {
  }

  public static void init() {
    if (Calendar.calendar().isPresent()) {
      CuriosApi.registerCurio(Calendar.calendar().get(), new CuriosCalendar());
    }
  }
}