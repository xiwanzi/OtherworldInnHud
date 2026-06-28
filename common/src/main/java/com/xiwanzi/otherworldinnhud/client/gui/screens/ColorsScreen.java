package com.xiwanzi.otherworldinnhud.client.gui.screens;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.components.boxes.ColorEditBox;
import com.xiwanzi.otherworldinnhud.client.gui.components.buttons.DefaultColorButton;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.rgb.BlueSlider;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.rgb.GreenSlider;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.rgb.RedSlider;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.rgb.RgbSlider;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.season.components.Seasons;
import com.xiwanzi.otherworldinnhud.util.Rgb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorsScreen extends OtherworldInnHudScreen {
  private static final Component SCREEN_TITLE = Common.translatedText("menu.otherworldinn_hud.color.title");
  private final List<ColorEditBox> seasonBoxes = new ArrayList<>();
  private int posX;
  private int posY;
  private boolean seasonColor;

  public ColorsScreen(Screen parentScreen) {
    super(parentScreen, SCREEN_TITLE);
    this.buttonWidth = 170;
    loadConfig();
  }

  public static ColorsScreen getInstance(Screen parentScreen) {
    return new ColorsScreen(parentScreen);
  }

  private static EnumSet<Seasons> seasonListSet() {
    EnumSet<Seasons> set = Seasons.SEASONS_ENUM_LIST.clone();
    set.remove(Seasons.NULL);

    if (!OtherworldInnHudClient.getShowTropicalSeason() || Common.fabricSeasonsLoaded()) {
      set.remove(Seasons.DRY);
      set.remove(Seasons.WET);
    }

    return set;
  }

  public void loadConfig() {
    seasonColor = OtherworldInnHudClient.getEnableSeasonNameColor();
  }

  @Override
  public void onDone() {
    seasonBoxes.forEach(editBox -> {
      if (Integer.parseInt(editBox.getValue()) != editBox.getColor()) {
        editBox.save();
      }
    });

    OtherworldInnHudClient.CLIENT_SPEC.save();
    super.onDone();
  }

  @Override
  public void onClose() {
    OtherworldInnHudClient.setEnableSeasonNameColor(seasonColor);
    super.onClose();
  }

  public int getBoxWidth() {
    int widgetCount = seasonListSet().size();
    int widgetTotalSize = ((80 + BUTTON_PADDING) * widgetCount);

    int boxWidth;
    if (this.width < widgetTotalSize) {
      boxWidth = 60;
    } else {
      boxWidth = 80;
    }

    return boxWidth;
  }

  private List<AbstractWidget> seasonWidget(int x, int y, Seasons season) {
    ColorEditBox colorBox = new ColorEditBox(this.font, x, y, getBoxWidth(), buttonHeight, season);
    y += colorBox.getHeight() + BUTTON_PADDING;
    x -= 1;
    y += buttonHeight + RgbSlider.SLIDER_PADDING;

    int initialR = Rgb.red(colorBox.getColor());
    RedSlider redSlider = new RedSlider(x, y, initialR, colorBox);
    y += redSlider.getHeight() + RgbSlider.SLIDER_PADDING;

    int initialG = Rgb.green(colorBox.getColor());
    GreenSlider greenSlider = new GreenSlider(x, y, initialG, colorBox);
    y += greenSlider.getHeight() + RgbSlider.SLIDER_PADDING;

    int initialB = Rgb.blue(colorBox.getColor());
    BlueSlider blueSlider = new BlueSlider(x, y, initialB, colorBox);
    y -= (greenSlider.getHeight() + redSlider.getHeight() + RgbSlider.SLIDER_PADDING + buttonHeight
        + RgbSlider.SLIDER_PADDING);

    @SuppressWarnings("checkstyle:Indentation")
    DefaultColorButton defaultButton = DefaultColorButton.builder(colorBox, press -> {
          int defaultColorInt = season.getDefaultColor();

          if (colorBox.getNewColor() != defaultColorInt) {
            int r = Rgb.red(defaultColorInt);
            int g = Rgb.green(defaultColorInt);
            int b = Rgb.blue(defaultColorInt);

            redSlider.setValue(r);
            greenSlider.setValue(g);
            blueSlider.setValue(b);
            colorBox.setValue(String.valueOf(defaultColorInt));
          }
        })
        .withPos(x, y)
        .build();

    seasonBoxes.add(colorBox);

    return new ArrayList<>(Arrays.asList(colorBox, defaultButton, redSlider, greenSlider, blueSlider));
  }

  @Override
  public void init() {
    super.init();

    int widgetWidth = getBoxWidth() + BUTTON_PADDING;
    int totalWidgetWidth = (seasonListSet().size() * widgetWidth) - BUTTON_PADDING;

    this.posX = (this.width / 2) - (totalWidgetWidth / 2);
    this.posY = MENU_PADDING + buttonHeight + BUTTON_PADDING + buttonHeight;

    seasonListSet().forEach(season -> {
      this.widgets.addAll(seasonWidget(this.posX, this.posY, season));
      this.posX += widgetWidth;
    });

    // Buttons
    CycleButton<Boolean> seasonColorButton = CycleButton.onOffBuilder(OtherworldInnHudClient.getEnableSeasonNameColor())
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.color.enableSeasonNameColor.tooltip"))
        .create(leftButtonX, MENU_PADDING, buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.color.enableSeasonNameColor.button"), (b, val) -> {
              OtherworldInnHudClient.setEnableSeasonNameColor(val);
              rebuildWidgets();
            });

    this.widgets.add(seasonColorButton);

    this.widgets.forEach(this::addRenderableWidget);
  }

  @Override
  public void tick() {
    super.tick();
  }

}