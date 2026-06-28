package com.xiwanzi.otherworldinnhud.client.gui;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.impl.season.mods.CommonSeasonHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public enum ShowDay {
  NONE(0, "none", "desc.otherworldinn_hud.hud.summary"),

  SHOW_DAY(1, "day", "desc.otherworldinn_hud.hud.detailed"),

  SHOW_WITH_TOTAL_DAYS(2, "totalDays", "desc.otherworldinn_hud.hud.detailed.total"),

  SHOW_WITH_MONTH(3, "month", "desc.otherworldinn_hud.hud.month");

  private final int idNum;
  private final String currentDayDisplay;
  private final Component dayDisplayName;
  private final String key;

  ShowDay(int id, String dayType, String key) {
    this.idNum = id;
    this.currentDayDisplay = dayType;
    this.dayDisplayName = Common.translatedText("desc.otherworldinn_hud.showday" + "." + dayType);
    this.key = key;
  }

  public static List<ShowDay> getValues() {
    List<ShowDay> values = new ArrayList<>(List.of(ShowDay.values()));

    if (!CommonSeasonHelper.commonSeasons.getHelper().isSeasonTiedWithSystemTime()) {
      values.remove(SHOW_WITH_MONTH.getId());
    }

    return values;
  }

  public int getId() {
    return this.idNum;
  }

  public String getDayDisplay() {
    return this.currentDayDisplay;
  }

  public Component getDayDisplayName() {
    return this.dayDisplayName;
  }

  public String getKey() {
    return this.key;
  }
}