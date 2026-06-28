package com.xiwanzi.otherworldinnhud.fabric.impl;

import com.xiwanzi.otherworldinnhud.client.gui.screens.MainOptionsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuOtherworldInnHud implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return MainOptionsScreen::getInstance;
  }
}