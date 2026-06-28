package com.xiwanzi.otherworldinnhud.client.gui;

import com.xiwanzi.otherworldinnhud.Common;
import net.minecraft.network.chat.Component;

public enum Location {
  TOP_LEFT(0, "desc.otherworldinn_hud.location.topLeft"),

  TOP_CENTER(1, "desc.otherworldinn_hud.location.topCenter"),

  TOP_RIGHT(2, "desc.otherworldinn_hud.location.topRight"),

  BOTTOM_LEFT(3, "desc.otherworldinn_hud.location.bottomLeft"),

  BOTTOM_RIGHT(4, "desc.otherworldinn_hud.location.bottomRight"),

  CUSTOM(5, "desc.otherworldinn_hud.location.custom");

  private final String key;
  private final Component locationName;
  private final int idNum;

  Location(int id, String key) {
    this.idNum = id;
    this.key = key;
    this.locationName = Common.translatedText(key);
  }

  public int getId() {
    return this.idNum;
  }

  public String getLocation() {
    return this.key;
  }

  public Component getLocationName() {
    return this.locationName;
  }
}