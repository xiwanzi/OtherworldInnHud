package com.xiwanzi.otherworldinnhud.fabric.event;

import com.xiwanzi.otherworldinnhud.client.KeyBindings;
import com.xiwanzi.otherworldinnhud.client.OtherworldInnHudClientCommon;
import com.xiwanzi.otherworldinnhud.fabric.client.overlays.OtherworldInnHudOverlay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class ClientEvents {
  private ClientEvents() {
  }

  // Key Bindings
  private static void registerKeyInputs() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      OtherworldInnHudClientCommon.optionsKeyInput();
    });
  }

  private static void registerKeyMappings() {
    KeyBindings.otherworldInnHudOptionsKeyMapping = KeyBindingHelper.registerKeyBinding(
        KeyBindings.otherworldInnHudOptionsKeyMapping);
  }

  private static void registerHud() {
    OtherworldInnHudOverlay.init();
  }

  public static void register() {
    registerKeyMappings();
    registerKeyInputs();
    registerHud();
  }
}

