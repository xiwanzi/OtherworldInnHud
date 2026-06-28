package com.xiwanzi.otherworldinnhud.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
  public static KeyMapping otherworldInnHudOptionsKeyMapping = new KeyMapping("desc.otherworldinn_hud.keybind.options",
                                                                       InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H,
                                                                       "desc.otherworldinn_hud.keybind.category");

  private KeyBindings() {
  }
}


