package com.xiwanzi.otherworldinnhud.client.gui.screens;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.Location;
import com.xiwanzi.otherworldinnhud.client.gui.ShowDay;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.BasicSlider;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.HudOffsetSlider;
import com.xiwanzi.otherworldinnhud.client.gui.components.sliders.HudScaleSlider;
import com.xiwanzi.otherworldinnhud.config.DefaultValues.Client;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudServer;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentFertility;
import com.xiwanzi.otherworldinnhud.impl.season.CurrentSeason;
import com.xiwanzi.otherworldinnhud.impl.season.components.Seasons;
import com.xiwanzi.otherworldinnhud.impl.season.components.SubSeasons;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

public class DisplayOptionsScreen extends OtherworldInnHudScreen {
  private static final Component SCREEN_TITLE = Common.translatedText("menu.otherworldinn_hud.season.title");
  private Location hudLocation;
  private int posX;
  private int posY;
  private int fabricSeasonsRow;
  private double seasonScale;
  private ShowDay showDay;
  private boolean seasonColor;
  private boolean showSubSeason;
  private boolean showTropicalSeason;
  private boolean showFertility;
  private boolean fertilityReplacesSeason;
  private boolean needCalendar;
  private boolean enableCalendarDetail;
  private boolean drawDefaultHud;
  private int dayLength;
  private int newDayLength;
  private CycleButton<Location> hudLocationButton;
  private CycleButton<Boolean> fertilityReplacesSeasonButton;
  private HudOffsetSlider sliderX;
  private HudOffsetSlider sliderY;
  private HudScaleSlider hudScaleSlider;
  private EditBox dayLengthBox;

  public DisplayOptionsScreen(Screen parentScreen) {
    super(parentScreen, SCREEN_TITLE);
    this.buttonWidth = 175;
  }

  public static DisplayOptionsScreen getInstance(Screen parentScreen) {
    return new DisplayOptionsScreen(parentScreen);
  }

  public void loadConfig() {
    drawDefaultHud = Common.drawDefaultHudMenu();
    hudLocation = OtherworldInnHudClient.getHudLocation();
    posX = OtherworldInnHudClient.getHudX();
    posY = OtherworldInnHudClient.getHudY();
    seasonScale = OtherworldInnHudClient.getHudScale();
    showDay = OtherworldInnHudClient.getShowDay();
    seasonColor = OtherworldInnHudClient.getEnableSeasonNameColor();
    showSubSeason = OtherworldInnHudClient.getShowSubSeason();
    showTropicalSeason = OtherworldInnHudClient.getShowTropicalSeason();

    if (Common.hasCalendarLoaded()) {
      needCalendar = OtherworldInnHudServer.getNeedCalendar();
      enableCalendarDetail = OtherworldInnHudServer.getCalendarDetailMode();
    }

    if (Common.fabricSeasonsLoaded()) {
      dayLength = OtherworldInnHudServer.getDayLength();
    }

    if (Common.sereneSeasonsLoaded()) {
      showFertility = OtherworldInnHudClient.getShowFertility();
      fertilityReplacesSeason = OtherworldInnHudClient.getFertilityReplacesSeason();
    }
  }

  public void saveConfig() {
    if (drawDefaultHud) {
      OtherworldInnHudClient.setHudLocation(hudLocationButton.getValue());
      OtherworldInnHudClient.setHudX(sliderX.getValueInt());
      OtherworldInnHudClient.setHudY(sliderY.getValueInt());
      OtherworldInnHudClient.setHudScale(hudScaleSlider.getValueDouble());
    }
    OtherworldInnHudClient.setShowDay(showDay);
    OtherworldInnHudClient.setEnableSeasonNameColor(seasonColor);
    OtherworldInnHudClient.setShowSubSeason(showSubSeason);

    if (Common.hasTropicalSeasons()) {
      OtherworldInnHudClient.setShowTropicalSeason(showTropicalSeason);
    }

    if (Common.clientSideConfig(this.minecraft)) {
      if (Common.hasCalendarLoaded()) {
        OtherworldInnHudServer.setCalendarDetailMode(enableCalendarDetail);
        OtherworldInnHudServer.setNeedCalendar(needCalendar);
      }

      if (Common.fabricSeasonsLoaded()) {
        OtherworldInnHudServer.setDayLength(Integer.parseInt(dayLengthBox.getValue()));
      }

      OtherworldInnHudServer.SERVER_SPEC.save();
    }

    if (Common.sereneSeasonsLoaded()) {
      OtherworldInnHudClient.setShowFertility(showFertility);
      OtherworldInnHudClient.setFertilityReplacesSeason(fertilityReplacesSeason);
    }

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

  public MutableComponent getModMenuText() {
    Seasons season = Seasons.SPRING;
    SubSeasons subSeason = SubSeasons.EARLY;
    long currentDate = 1;
    int currentDuration = this.showSubSeason
                          ? 8
                          : 24;

    MutableComponent seasonIcon = Common.translatedText("desc.otherworldinn_hud.hud.icon", season.getIconChar());
    MutableComponent seasonText = CurrentSeason.getText(season, subSeason, this.showDay, currentDate, currentDuration,
                                                        this.showSubSeason).copy();
    Style seasonFormat = Style.EMPTY;

    if (this.seasonColor) {
      if (OtherworldInnHudClient.getEnableSeasonNameColor()) {
        seasonFormat = Style.EMPTY.withColor(season.getSeasonColor());
      }
    }

    return Common.translatedText("desc.otherworldinn_hud.hud.combined", seasonIcon.withStyle(Common.SEASON_ICON_STYLE),
                                 seasonText.withStyle(seasonFormat));
  }

  // TODO: Need to fix Tropical Seasons option not updating in config screen
  @Override
  public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    super.render(graphics, mouseX, mouseY, partialTicks);
    if (this.minecraft == null) {
      return;
    }

    MutableComponent seasonCombined;

    if (this.minecraft.player == null) {
      seasonCombined = this.getModMenuText();
    } else {
      seasonCombined = CurrentSeason.getInstance(this.minecraft).getConfigText(showDay, showSubSeason, seasonColor);
    }

    posX = Client.DEFAULT_X_OFFSET;
    posY = Client.DEFAULT_Y_OFFSET;

    if (Common.sereneSeasonsLoaded()) {
      fertilityReplacesSeasonButton.active = showFertility;
    }

    if (drawDefaultHud) {
      hudScaleSlider.visible = true;
      sliderX.visible = true;
      sliderY.visible = true;

      boolean customLocation = (hudLocationButton.getValue() == Location.CUSTOM);
      sliderX.active = customLocation;
      sliderY.active = customLocation;

      seasonScale = hudScaleSlider.getValueDouble();
      int componentWidth = (int) (this.font.width(seasonCombined) * seasonScale);
      int componentHeight = (int) (this.font.lineHeight * seasonScale);

      switch (hudLocation) {
        case TOP_LEFT:
          posX = Client.DEFAULT_X_OFFSET;
          posY = Client.DEFAULT_Y_OFFSET;
          break;

        case TOP_CENTER:
          posX = (int) ((((double) width / 2) - ((double) componentWidth / 2)) / seasonScale);
          posY = Client.DEFAULT_Y_OFFSET;
          break;

        case TOP_RIGHT:
          posX = (int) ((width - componentWidth - Client.DEFAULT_X_OFFSET) / seasonScale);
          posY = Client.DEFAULT_Y_OFFSET;
          break;

        case BOTTOM_LEFT:
          posX = Client.DEFAULT_X_OFFSET;
          posY = (int) (((height - componentHeight - Client.DEFAULT_Y_OFFSET)) / seasonScale);
          break;

        case BOTTOM_RIGHT:
          posX = (int) (((width - componentWidth - Client.DEFAULT_X_OFFSET)) / seasonScale);
          posY = (int) (((height - componentHeight - Client.DEFAULT_Y_OFFSET)) / seasonScale);
          break;

        case CUSTOM:
          posX = (sliderX.getValueInt());
          posY = (sliderY.getValueInt());
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + hudLocation);
      }
    }

    if (Common.sereneSeasonsLoaded()) {
      fertilityReplacesSeasonButton.active = showFertility;
    }

    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, 50);
    graphics.pose().scale((float) seasonScale, (float) seasonScale, 1.0F);
    graphics.drawString(font, seasonCombined, posX, posY, 0xffffff);

    if (Common.fabricSeasonsLoaded() && Common.clientSideConfig(this.minecraft)) {
      drawColumnHeading(graphics, Common.literalText("Fabric Seasons Day Length"), Side.LEFT, (fabricSeasonsRow));
    }

    if (minecraft.player != null && CurrentFertility.getInstance(this.minecraft).shouldDrawNewLine()) {
      MutableComponent fertility = CurrentFertility.getInstance(this.minecraft).getHudText();

      posY += this.font.lineHeight;
      graphics.drawString(font, fertility, posX, posY, 0xffffff);
    }

    graphics.pose().popPose();
  }

  private int maxWidth(MutableComponent seasonText) {
    int textWidth = this.font.width(seasonText);

    return (int) ((this.width - (textWidth * seasonScale)) / seasonScale);
  }

  private int maxHeight() {
    int textHeight = this.font.lineHeight;

    return (int) ((this.height - (textHeight * seasonScale)) / seasonScale);
  }

  // TODO: Make subscreens for Season options, fertility options. Make sure the preview displays on both

  private void defaultHudButtons() {
    if (drawDefaultHud) {
      row += 1;

      hudLocationButton = CycleButton.builder(Location::getLocationName)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.hudLocation.tooltip"))
          .withValues(Location.values())
          .withInitialValue(hudLocation)
          .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.hudLocation.button"),
                  (b, val) -> this.hudLocation = val);

      hudScaleSlider = HudScaleSlider.builder(Common.translatedText("menu.otherworldinn_hud.season.scale.slider"))
          .withTooltip(Common.newTooltip("menu.otherworldinn_hud.season.scale.tooltip"))
          .withValueRange(Client.HUD_SCALE_MIN, Client.HUD_SCALE_MAX)
          .withInitialValue(seasonScale)
          .withDefaultValue(Client.DEFAULT_HUD_SCALE).withStepSize(0.5).withPrecision(1)
          .withBounds(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight)
          .build();

      row += 1;

      MutableComponent seasonCombined;

      if (this.minecraft.player == null) {
        seasonCombined = this.getModMenuText();
      } else {
        seasonCombined = CurrentSeason.getInstance(this.minecraft).getConfigText(showDay, showSubSeason, seasonColor);
      }

      sliderX = HudOffsetSlider.builder(Common.translatedText("menu.otherworldinn_hud.season.xOffset.slider"))
          .withTooltip(Common.newTooltip("menu.otherworldinn_hud.season.xOffset.tooltip"))
          .withValues(0, this.maxWidth(seasonCombined), posX, Client.DEFAULT_X_OFFSET)
          .withBounds(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth / 2 - BasicSlider.SLIDER_PADDING,
                      buttonHeight)
          .build();

      sliderY = HudOffsetSlider.builder(Common.translatedText("menu.otherworldinn_hud.season.yOffset.slider"))
          .withTooltip(Common.newTooltip("menu.otherworldinn_hud.season.yOffset.tooltip"))
          .withValues(0, this.maxHeight(), posY, Client.DEFAULT_Y_OFFSET)
          .withBounds(rightButtonX + buttonWidth / 2 + BasicSlider.SLIDER_PADDING, (buttonStartY + (row * offsetY)),
                      buttonWidth / 2 - BasicSlider.SLIDER_PADDING, buttonHeight)
          .build();

      widgets.addAll(Arrays.asList(hudLocationButton, hudScaleSlider, sliderX, sliderY));
    }
  }

  private void seasonButtons() {
    row += 1;

    CycleButton<ShowDay> showDayButton = CycleButton.builder(ShowDay::getDayDisplayName)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.showDay.tooltip"))
        .withValues(ShowDay.getValues())
        .withInitialValue(showDay)
        .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.season.showDay.button"), (b, val) -> this.showDay = val);

    CycleButton<Boolean> seasonColorButton = CycleButton.onOffBuilder(seasonColor)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.color.enableSeasonNameColor.tooltip"))
        .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.color.enableSeasonNameColor.button"),
                (b, val) -> this.seasonColor = val);
    widgets.addAll(Arrays.asList(showDayButton, seasonColorButton));

    row += 1;

    CycleButton<Boolean> showSubSeasonButton = CycleButton.onOffBuilder(showSubSeason)
        .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.showSubSeason.tooltip"))
        .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                Common.translatedText("menu.otherworldinn_hud.season.showSubSeason.button"),
                (b, val) -> this.showSubSeason = val);

    if (Common.fabricSeasonsLoaded() && this.minecraft != null) {
      int seasonLength = Services.SEASON.currentFabricSeasonLength(this.minecraft.player);

      if ((seasonLength % 3) != 0) {
        showSubSeasonButton.active = false;
        showSubSeasonButton.setTooltip(
            Common.newTooltip("menu.otherworldinn_hud.season.showSubSeason.tooltip.error", seasonLength, seasonLength * 24000));
      }
    }
    widgets.add(showSubSeasonButton);

    if (Common.hasTropicalSeasons()) {
      CycleButton<Boolean> showTropicalSeasonButton = CycleButton.onOffBuilder(showTropicalSeason)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.showTropicalSeason.tooltip"))
          .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.showTropicalSeason.button"),
                  (b, val) -> this.showTropicalSeason = val);
      widgets.add(showTropicalSeasonButton);
    }
  }

  private void calendarButtons() {
    if (Common.hasCalendarLoaded()) {
      row += 1;

      CycleButton<Boolean> needCalendarButton = CycleButton.onOffBuilder(needCalendar)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.needCalendar.tooltip"))
          .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.needCalendar.button"),
                  (b, val) -> this.needCalendar = val);

      needCalendarButton.active = Common.clientSideConfig(this.minecraft);

      CycleButton<Boolean> calendarDetailModeButton = CycleButton.onOffBuilder(enableCalendarDetail)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.calendarDetail.tooltip"))
          .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.calendarDetail.button"),
                  (b, val) -> this.enableCalendarDetail = val);

      if (!Common.clientSideConfig(this.minecraft)) {
        needCalendarButton.active = false;
        needCalendarButton.setTooltip(Common.newTooltip("menu.otherworldinn_hud.season.serverSide.tooltip"));

        calendarDetailModeButton.active = false;
        calendarDetailModeButton.setTooltip(Common.newTooltip("menu.otherworldinn_hud.season.serverSide.tooltip"));
      }

      widgets.addAll(Arrays.asList(needCalendarButton, calendarDetailModeButton));
    }
  }

  private void fertilityButtons() {
    if (Common.sereneSeasonsLoaded()) {
      row += 1;

      CycleButton<Boolean> showFertilityButton = CycleButton.onOffBuilder(showFertility)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.showFertility.tooltip"))
          .create(leftButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.showFertility.button"),
                  (b, val) -> this.showFertility = val);

      fertilityReplacesSeasonButton = CycleButton.onOffBuilder(fertilityReplacesSeason)
          .withTooltip(t -> Common.newTooltip("menu.otherworldinn_hud.season.fertilityReplacesSeason.tooltip"))
          .create(rightButtonX, (buttonStartY + (row * offsetY)), buttonWidth, buttonHeight,
                  Common.translatedText("menu.otherworldinn_hud.season.fertilityReplacesSeason.button"),
                  (b, val) -> this.fertilityReplacesSeason = val);

      widgets.addAll(Arrays.asList(showFertilityButton, fertilityReplacesSeasonButton));
    }
  }

  private void fabricSeasonsButtons() {
    if (Common.fabricSeasonsLoaded()) {
      row += 2;
      fabricSeasonsRow = row;

      dayLengthBox = new EditBox(this.font, leftButtonX + 1, (buttonStartY + (row * offsetY)), buttonWidth - 2,
                                 buttonHeight, Common.literalText(String.valueOf(dayLength)));
      dayLengthBox.setMaxLength(10);
      dayLengthBox.setValue(String.valueOf(dayLength));
      dayLengthBox.setResponder((lengthString) -> {
        if (validate(lengthString)) {
          dayLengthBox.setTextColor(0xffffff);
          int currentLength = Integer.parseInt(lengthString);

          if (currentLength != this.newDayLength) {
            this.newDayLength = currentLength;
            dayLengthBox.setValue(lengthString);
          }

          doneButton.active = true;
        } else {
          dayLengthBox.setTextColor(16733525);
          doneButton.active = false;
        }
      });
      dayLengthBox.setHint(Common.literalText("" + dayLength).withStyle(ChatFormatting.DARK_GRAY));
      dayLengthBox.visible = Common.clientSideConfig(this.minecraft);

      widgets.add(dayLengthBox);
    }
  }

  @Override
  public void init() {
    loadConfig();
    super.init();

    row = -1;
    defaultHudButtons();
    seasonButtons();
    calendarButtons();
    fertilityButtons();
    fabricSeasonsButtons();

    widgets.forEach(this::addRenderableWidget);
  }

  private boolean inBounds(int length) {
    int minInt = 0;

    return length >= minInt;
  }

  public boolean validate(String length) {
    try {
      int dayLength = Integer.parseInt(length);
      return this.inBounds(dayLength);
    } catch (NumberFormatException formatException) {
      return false;
    }
  }
}
