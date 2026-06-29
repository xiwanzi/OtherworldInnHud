package com.xiwanzi.otherworldinnhud.client.gui.screens;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.TaskHudLocation;
import com.xiwanzi.otherworldinnhud.client.gui.components.buttons.MenuButton;
import com.xiwanzi.otherworldinnhud.client.gui.components.buttons.MenuButton.MenuButtons;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.Arrays;
import journeymap.client.ui.UIManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class MainOptionsScreen extends OtherworldInnHudScreen {
  private static final Component SCREEN_TITLE = Common.translatedText("menu.otherworldinn_hud.main.title");
  private static final Component MINIMAP_SETTINGS = Common.translatedText("menu.otherworldinn_hud.main.minimap.options");
  private static final Component JOURNEYMAP = Common.translatedText("menu.otherworldinn_hud.main.journeymap.title");
  private static final Component TASK_HUD_SETTINGS = Common.translatedText("menu.otherworldinn_hud.main.task.options");
  MenuButton seasonButton;
  MenuButton colorButton;
  CycleButton<Boolean> enableMinimapIntegrationButton;
  CycleButton<Boolean> showMinimapHiddenButton;
  CycleButton<Boolean> enableTaskHudButton;
  CycleButton<TaskHudLocation> taskHudLocationButton;
  Button journeyMapButton;
  private boolean enableMod;
  private boolean enableTaskHud;
  private boolean showMinimapHidden;
  private boolean enableMinimapIntegration;
  private TaskHudLocation taskHudLocation;
  private int minimapRow;
  private int taskHudRow;
  private int journeyMapRow;

  public MainOptionsScreen(Screen parentScreen) {
    super(parentScreen, SCREEN_TITLE);
    loadConfig();
    this.buttonWidth = 170;
  }

  public static MainOptionsScreen getInstance(Screen parentScreen) {
    return new MainOptionsScreen(parentScreen);
  }

  public void loadConfig() {
    enableMod = OtherworldInnHudClient.getEnableMod();
    enableTaskHud = OtherworldInnHudClient.getEnableTaskHud();
    taskHudLocation = OtherworldInnHudClient.getTaskHudLocation();
    showMinimapHidden = OtherworldInnHudClient.getShowDefaultWhenMinimapHidden();
    enableMinimapIntegration = OtherworldInnHudClient.getEnableMinimapIntegration();
  }

  public void saveConfig() {
    OtherworldInnHudClient.setEnableMod(enableMod);
    OtherworldInnHudClient.setEnableTaskHud(enableTaskHud);
    OtherworldInnHudClient.setTaskHudLocation(taskHudLocation);
    OtherworldInnHudClient.setEnableMinimapIntegration(enableMinimapIntegration);
    OtherworldInnHudClient.setShowDefaultWhenMinimapHidden(showMinimapHidden);
    OtherworldInnHudClient.CLIENT_SPEC.save();
  }

  @Override
  public void onDone() {
    saveConfig();
    super.onDone();
  }

  @Override
  public void onClose() {
    super.onClose();
  }

  @Override
  public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    super.render(graphics, mouseX, mouseY, partialTicks);

    drawHeading(graphics, MINIMAP_SETTINGS, minimapRow);
    drawHeading(graphics, TASK_HUD_SETTINGS, taskHudRow);

    if (Services.PLATFORM.isModLoaded("journeymap")) {
      drawHeading(graphics, JOURNEYMAP, journeyMapRow);

      journeyMapButton.active = enableMod;
    }

    seasonButton.active = enableMod;
    colorButton.active = enableMod;
    enableMinimapIntegrationButton.active = enableMod;
    showMinimapHiddenButton.active = enableMod;
    enableTaskHudButton.active = enableMod;
    taskHudLocationButton.active = enableMod && enableTaskHud;
  }

  public void seasonHudOptionsButtons() {
    row += 1;
    seasonButton = MenuButton.builder(MenuButtons.SEASON, this, DisplayOptionsScreen.getInstance(this))
        .withTooltip(Common.newTooltip("menu.otherworldinn_hud.main.season.tooltip"))
        .withPos(leftButtonX, (buttonStartY + (row * offsetY))).withWidth(buttonWidth)
        .build();

    colorButton = MenuButton.builder(MenuButtons.COLORS, this, ColorsScreen.getInstance(this))
        .withTooltip(Common.newTooltip("menu.otherworldinn_hud.main.color.tooltip"))
        .withPos(rightButtonX, (buttonStartY + (row * offsetY))).withWidth(buttonWidth)
        .build();
    widgets.addAll(Arrays.asList(seasonButton, colorButton));
  }

  public void taskHudOptionsButtons() {
    row += 2;
    taskHudRow = row;

    enableTaskHudButton = CycleButton.onOffBuilder(enableTaskHud)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.main.enableTaskHud.tooltip"))
        .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.main.enableTaskHud.button"),
                (b, val) -> enableTaskHud = val);

    taskHudLocationButton = CycleButton.builder(TaskHudLocation::getDisplayName)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.main.taskHudLocation.tooltip"))
        .withValues(TaskHudLocation.values())
        .withInitialValue(taskHudLocation)
        .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.main.taskHudLocation.button"),
                (b, val) -> taskHudLocation = val);

    widgets.addAll(Arrays.asList(enableTaskHudButton, taskHudLocationButton));
  }

  public void minimapOptionsButtons() {
    row += 2;
    minimapRow = row;

    enableMinimapIntegrationButton = CycleButton.onOffBuilder(enableMinimapIntegration)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.main.minimapIntegration.tooltip"))
        .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.main.enableMinimapIntegration.button"),
                (b, val) -> enableMinimapIntegration = val);

    showMinimapHiddenButton = CycleButton.onOffBuilder(showMinimapHidden)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.main.showMinimapHidden.tooltip"))
        .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.main.showMinimapHidden.button"),
                (b, val) -> showMinimapHidden = val);

    widgets.addAll(Arrays.asList(enableMinimapIntegrationButton, showMinimapHiddenButton));
  }

  public void journeymapOptions() {
    if (Services.PLATFORM.isModLoaded("journeymap")) {
      row += 2;
      journeyMapRow = row;

      journeyMapButton = MenuButton.builder(MenuButtons.JOURNEYMAP,
                                            (button) -> UIManager.INSTANCE.openAddonOptionsEditor(this, true))
          .withTooltip(Common.newTooltip("menu.otherworldinn_hud.main.journeymap.options.tooltip"))
          .withPos(leftButtonX, (buttonStartY + (row * offsetY))).withWidth(buttonWidth)
          .build();

      widgets.add(journeyMapButton);
    }
  }

  @Override
  public void init() {
    super.init();

    int enableModWidth = font.width(Common.translatedText("menu.otherworldinn_hud.main.enableMod.button").append(": OFF")) + 8;

    CycleButton<Boolean> enableModButton = CycleButton.onOffBuilder(enableMod)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.main.enableMod.tooltip"))
        .create(this.width - enableModWidth - TITLE_PADDING / 2, TITLE_PADDING / 2, enableModWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.main.enableMod.button"), (b, val) -> enableMod = val);
    widgets.add(enableModButton);

    row = -1;
    seasonHudOptionsButtons();
    taskHudOptionsButtons();
    minimapOptionsButtons();
    journeymapOptions();

    widgets.forEach(this::addRenderableWidget);
  }
}
