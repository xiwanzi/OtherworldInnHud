package com.xiwanzi.otherworldinnhud.client.gui;

import com.xiwanzi.otherworldinnhud.Common;
import net.minecraft.network.chat.Component;

public enum TaskHudStyle {
  CLEAN_CLOTH("desc.otherworldinn_hud.task.style.clean_cloth"),
  WARM_CLOTH("desc.otherworldinn_hud.task.style.warm_cloth"),
  HEAVY_FRAME("desc.otherworldinn_hud.task.style.heavy_frame");

  private final String key;
  private final Component displayName;

  TaskHudStyle(String key) {
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
