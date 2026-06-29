package com.xiwanzi.otherworldinnhud.client.gui;

import com.xiwanzi.otherworldinnhud.Common;
import net.minecraft.network.chat.Component;

public enum TaskHudLocation {
  RIGHT("desc.otherworldinn_hud.task.location.right"),

  CENTER("desc.otherworldinn_hud.task.location.center");

  private final String key;
  private final Component displayName;

  TaskHudLocation(String key) {
    this.key = key;
    this.displayName = Common.translatedText(key);
  }

  public String getKey() {
    return this.key;
  }

  public Component getDisplayName() {
    return this.displayName;
  }
}
