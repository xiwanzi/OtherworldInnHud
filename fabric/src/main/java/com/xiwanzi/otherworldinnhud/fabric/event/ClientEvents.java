package com.xiwanzi.otherworldinnhud.fabric.event;

import com.xiwanzi.otherworldinnhud.fabric.client.overlays.OtherworldInnHudOverlay;

public class ClientEvents {
  private ClientEvents() {
  }

  private static void registerHud() {
    OtherworldInnHudOverlay.init();
  }

  public static void register() {
    registerHud();
  }
}
