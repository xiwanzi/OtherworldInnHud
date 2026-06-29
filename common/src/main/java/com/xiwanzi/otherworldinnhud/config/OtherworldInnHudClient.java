package com.xiwanzi.otherworldinnhud.config;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.Location;
import com.xiwanzi.otherworldinnhud.client.gui.ShowDay;
import com.xiwanzi.otherworldinnhud.config.DefaultValues.Client;
import com.xiwanzi.otherworldinnhud.util.StringLine;
import java.util.Arrays;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class OtherworldInnHudClient {
  public static final ModConfigSpec CLIENT_SPEC;
  private static ConfigValue<Boolean> enableMod;
  private static ConfigValue<Location> hudLocation;
  private static ConfigValue<Integer> hudX;
  private static ConfigValue<Integer> hudY;
  private static ConfigValue<Double> hudScale;
  private static ConfigValue<Boolean> enableSeasonNameColor;
  private static ConfigValue<Integer> springColor;
  private static ConfigValue<Integer> summerColor;
  private static ConfigValue<Integer> autumnColor;
  private static ConfigValue<Integer> winterColor;
  private static ConfigValue<Integer> dryColor;
  private static ConfigValue<Integer> wetColor;
  private static ConfigValue<Boolean> showTropicalSeason;
  private static ConfigValue<Boolean> showSubSeason;
  private static ConfigValue<ShowDay> showDay;
  private static ConfigValue<Boolean> showFertility;
  private static ConfigValue<Boolean> fertilityReplacesSeason;
  private static ConfigValue<Boolean> enableMinimapIntegration;
  private static ConfigValue<Boolean> showDefaultWhenMinimapHidden;
  private static ConfigValue<Boolean> enableTaskHud;
  private static ConfigValue<Integer> taskHudX;
  private static ConfigValue<Integer> taskHudY;
  private static ConfigValue<Double> taskHudScale;
  private static ConfigValue<Integer> taskHudMinimapReservedHeight;
  private static ConfigValue<Integer> taskHudGapLines;

  static {
    ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    setupConfig(builder);
    CLIENT_SPEC = builder.build();
  }

  private OtherworldInnHudClient() {
  }

  private static void setupConfig(ModConfigSpec.Builder builder) {
    builder.push("Otherworld Inn HUD");
    enableMod = builder.comment(StringLine.builder()
                                    .addLine("Enable the mod?")
                                    .addLine("(true/false)")
                                    .lastLine("Default is " + Client.DEFAULT_ENABLE_MOD + "."))
        .define("enable_mod", Client.DEFAULT_ENABLE_MOD);

    builder.push("HUD");
    hudLocation = builder.comment(StringLine.builder()
                                      .addLine("Where to display the Hud when no minimap is installed.")
                                      .lastLine("Default is " + Client.DEFAULT_HUD_LOCATION + "."))
        .defineEnum("hud_location", Client.DEFAULT_HUD_LOCATION);

    hudX = builder.comment(StringLine.builder()
                               .addLine("The horizontal offset of the HUD when no minimap is installed (in pixels)")
                               .addLine("'hudLocation' must be set to 'CUSTOM' to take effect")
                               .lastLine("Default is " + Client.DEFAULT_X_OFFSET + "."))
        .define("hud_x_position", Client.DEFAULT_X_OFFSET);

    hudY = builder.comment(StringLine.builder()
                               .addLine("The vertical offset of the HUD when no minimap is installed (in pixels)")
                               .addLine("'hudLocation' must be set to 'CUSTOM' to take effect")
                               .lastLine("Default is " + Client.DEFAULT_Y_OFFSET + "."))
        .define("hud_y_position", Client.DEFAULT_Y_OFFSET);

    hudScale = builder.comment(StringLine.builder()
                                   .addLine("The scale of the HUD when no minimap is installed.")
                                   .lastLine("Default is " + Client.DEFAULT_HUD_SCALE + "."))
        .defineInRange("hud_scale", Client.DEFAULT_HUD_SCALE, Client.HUD_SCALE_MIN, Client.HUD_SCALE_MAX);

    builder.push("Colors");
    enableSeasonNameColor = builder.comment(StringLine.builder()
                                                .addLine("Display the season name in a color?")
                                                .addLine("(true/false)")
                                                .lastLine("Default is " + Client.DEFAULT_SEASON_NAME_COLOR + "."))
        .define("season_name_color", Client.DEFAULT_SEASON_NAME_COLOR);

    springColor = builder.comment(StringLine.builder()
                                      .addLine("The RGB color (decimal) for spring.")
                                      .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                      .lastLine("Default is " + Client.DEFAULT_SPRING_COLOR + "."))
        .defineInRange("spring_color", Client.DEFAULT_SPRING_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);

    summerColor = builder.comment(StringLine.builder()
                                      .addLine("The RGB color (decimal) for summer.")
                                      .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                      .lastLine("Default is " + Client.DEFAULT_SUMMER_COLOR + "."))
        .defineInRange("summer_color", Client.DEFAULT_SUMMER_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);

    autumnColor = builder.comment(StringLine.builder()
                                      .addLine("The RGB color (decimal) for autumn.")
                                      .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                      .lastLine("Default is " + Client.DEFAULT_AUTUMN_COLOR + "."))
        .defineInRange("autumn_color", Client.DEFAULT_AUTUMN_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);

    winterColor = builder.comment(StringLine.builder()
                                      .addLine("The RGB color (decimal) for winter.")
                                      .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                      .lastLine("Default is " + Client.DEFAULT_WINTER_COLOR + "."))
        .defineInRange("winter_color", Client.DEFAULT_WINTER_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);

    dryColor = builder.comment(StringLine.builder()
                                   .addLine("The RGB color (decimal) for dry tropical season.")
                                   .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                   .lastLine("Default is " + Client.DEFAULT_DRY_COLOR + "."))
        .defineInRange("dry_color", Client.DEFAULT_DRY_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);

    wetColor = builder.comment(StringLine.builder()
                                   .addLine("The RGB color (decimal) for wet tropical season.")
                                   .addLine("(256 * 256 * r) + (256 * g) + (b) is the formula.")
                                   .lastLine("Default is " + Client.DEFAULT_WET_COLOR + "."))
        .defineInRange("wet_color", Client.DEFAULT_WET_COLOR, Client.COLOR_MIN, Client.COLOR_MAX);
    builder.pop();
    builder.pop();

    builder.push("Season");
    showTropicalSeason = builder.comment(StringLine.builder()
                                             .addLine("Show the Tropical season (Wet/Dry) while in Tropical Biomes.")
                                             .addLine("This will not change the season behavior in the biomes, just")
                                             .addLine("what is displayed on the Hud")
                                             .addLine("(true/false)")
                                             .lastLine("Default is " + Client.DEFAULT_SHOW_TROPICAL_SEASON + "."))
        .define("enable_show_tropical_season", Client.DEFAULT_SHOW_TROPICAL_SEASON);

    showSubSeason = builder.comment(StringLine.builder()
                                        .addLine("Show sub-season instead of the basic season?")
                                        .addLine(("i.e. Early Winter, Mid Autumn, Late Spring"))
                                        .addLine("(true/false)")
                                        .addLine("")
                                        .addLine("If using Fabric Seasons: ")
                                        .addLine("You will want to change the 'seasonLength' options in the")
                                        .addLine("Fabric Seasons config (seasons.json) to be divisible by 3")
                                        .addLine("It defaults to 672000 ticks (28 days)")
                                        .addLine("")
                                        .lastLine("Default is " + Client.DEFAULT_SHOW_SUB_SEASON + "."))
        .define("enable_show_sub_season", Client.DEFAULT_SHOW_SUB_SEASON);

    if (Common.fabricSeasonsLoaded()) {
      showDay = builder.comment(StringLine.builder()
                                    .addLine("Show the current day of the season/sub-season?")
                                    .lastLine("Default is " + Client.DEFAULT_SHOW_DAY + "."))
          .defineEnum("enable_show_day", ShowDay.SHOW_DAY,
                      Arrays.asList(ShowDay.NONE, ShowDay.SHOW_DAY, ShowDay.SHOW_WITH_TOTAL_DAYS,
                                    ShowDay.SHOW_WITH_MONTH));
    }

    if (!Common.fabricSeasonsLoaded()) {
      showDay = builder.comment(StringLine.builder()
                                    .addLine("Show the current day of the season/sub-season?")
                                    .lastLine("Default is " + Client.DEFAULT_SHOW_DAY + "."))
          .defineEnum("enable_show_day", Client.DEFAULT_SHOW_DAY,
                      Arrays.asList(ShowDay.NONE, ShowDay.SHOW_DAY, ShowDay.SHOW_WITH_TOTAL_DAYS));
    }

    showFertility = builder.comment(StringLine.builder()
                                        .addLine("Show the current fertility of the biome?")
                                        .addLine("Only shows if not the default fertility of the season")
                                        .lastLine("Default is " + Client.DEFAULT_SHOW_FERTILITY + "."))
        .define("enable_show_fertility", Client.DEFAULT_SHOW_FERTILITY);

    fertilityReplacesSeason = builder.comment(StringLine.builder()
                                                  .addLine("If the season name should be replaced with the fertility")
                                                  .addLine("value when it differs from the default of the biome")
                                                  .lastLine(
                                                      "Default is " + Client.DEFAULT_FERTILITY_REPLACES_SEASON + "."))
        .define("enable_fertility_replaces_season", Client.DEFAULT_FERTILITY_REPLACES_SEASON);

    builder.pop();

    builder.push("Minimap");
    enableMinimapIntegration = builder.comment(StringLine.builder()
                                                   .addLine("Enable integration with minimap mods?")
                                                   .addLine("(true/false)")
                                                   .lastLine(
                                                       "Default is " + Client.DEFAULT_ENABLE_MINIMAP_INTEGRATION + "."))
        .define("enable_minimap_integration", Client.DEFAULT_ENABLE_MINIMAP_INTEGRATION);

    showDefaultWhenMinimapHidden = builder.comment(StringLine.builder()
                                                       .addLine(
                                                           "Show the default Otherworld Inn HUD when the minimap is hidden?")
                                                       .addLine("(true/false)")
                                                       .lastLine("Default is "
                                                                     + Client.DEFAULT_SHOW_DEFAULT_WHEN_MINIMAP_HIDDEN
                                                                     + "."))
        .define("enable_show_minimap_hidden", Client.DEFAULT_SHOW_DEFAULT_WHEN_MINIMAP_HIDDEN);
    builder.pop();

    builder.push("Task HUD");
    enableTaskHud = builder.comment(StringLine.builder()
                                      .addLine("Show active story wishes and town commissions on the right side?")
                                      .addLine("(true/false)")
                                      .lastLine("Default is " + Client.DEFAULT_ENABLE_TASK_HUD + "."))
        .define("enable_task_hud", Client.DEFAULT_ENABLE_TASK_HUD);

    taskHudX = builder.comment(StringLine.builder()
                                  .addLine("Right-side horizontal margin for the task HUD (in pixels).")
                                  .lastLine("Default is " + Client.DEFAULT_TASK_HUD_X_OFFSET + "."))
        .defineInRange("task_hud_x_margin", Client.DEFAULT_TASK_HUD_X_OFFSET, 0, 512);

    taskHudY = builder.comment(StringLine.builder()
                                  .addLine("Top margin for the task HUD when no visible minimap is present (in pixels).")
                                  .lastLine("Default is " + Client.DEFAULT_TASK_HUD_Y_OFFSET + "."))
        .defineInRange("task_hud_y_margin", Client.DEFAULT_TASK_HUD_Y_OFFSET, 0, 512);

    taskHudScale = builder.comment(StringLine.builder()
                                      .addLine("The scale of the right-side task HUD.")
                                      .lastLine("Default is " + Client.DEFAULT_TASK_HUD_SCALE + "."))
        .defineInRange("task_hud_scale", Client.DEFAULT_TASK_HUD_SCALE, Client.HUD_SCALE_MIN, Client.HUD_SCALE_MAX);

    taskHudMinimapReservedHeight = builder.comment(StringLine.builder()
                                                   .addLine("Top area reserved when a visible minimap is detected.")
                                                   .lastLine(
                                                       "Default is "
                                                           + Client.DEFAULT_TASK_HUD_MINIMAP_RESERVED_HEIGHT + "."))
        .defineInRange("task_hud_minimap_reserved_height", Client.DEFAULT_TASK_HUD_MINIMAP_RESERVED_HEIGHT, 0, 512);

    taskHudGapLines = builder.comment(StringLine.builder()
                                     .addLine("Blank line count between stacked task HUD blocks.")
                                     .lastLine("Default is " + Client.DEFAULT_TASK_HUD_GAP_LINES + "."))
        .defineInRange("task_hud_gap_lines", Client.DEFAULT_TASK_HUD_GAP_LINES, 0, 8);
    builder.pop();
    builder.pop();
  }

  private static <T> T getOrDefault(ConfigValue<T> config) {
    if (CLIENT_SPEC.isLoaded()) {
      return config.get();
    } else {
      return config.getDefault();
    }
  }

  // Otherworld Inn HUD
  public static boolean getEnableMod() {
    return getOrDefault(enableMod);
  }

  public static void setEnableMod(boolean enable) {
    OtherworldInnHudClient.enableMod.set(enable);
  }

  // HUD
  public static Location getHudLocation() {
    return getOrDefault(hudLocation);
  }

  public static void setHudLocation(Location location) {
    OtherworldInnHudClient.hudLocation.set(location);
  }

  public static int getHudX() {
    return getOrDefault(hudX);
  }

  public static void setHudX(int x) {
    OtherworldInnHudClient.hudX.set(x);
  }

  public static int getHudY() {
    return getOrDefault(hudY);
  }

  public static void setHudY(int y) {
    OtherworldInnHudClient.hudY.set(y);
  }

  public static double getHudScale() {
    return getOrDefault(hudScale);

  }

  public static void setHudScale(double scale) {
    OtherworldInnHudClient.hudScale.set(scale);
  }

  // Colors
  public static boolean getEnableSeasonNameColor() {
    return getOrDefault(enableSeasonNameColor);
  }

  public static void setEnableSeasonNameColor(boolean enable) {
    OtherworldInnHudClient.enableSeasonNameColor.set(enable);
  }

  public static int getSpringColor() {
    return getOrDefault(springColor);
  }

  public static void setSpringColor(int rgbColor) {
    OtherworldInnHudClient.springColor.set(rgbColor);
  }

  public static int getSummerColor() {
    return getOrDefault(summerColor);
  }

  public static void setSummerColor(int rgbColor) {
    OtherworldInnHudClient.summerColor.set(rgbColor);
  }

  public static int getAutumnColor() {
    return getOrDefault(autumnColor);
  }

  public static void setAutumnColor(int rgbColor) {
    OtherworldInnHudClient.autumnColor.set(rgbColor);
  }

  public static int getWinterColor() {
    return getOrDefault(winterColor);
  }

  public static void setWinterColor(int rgbColor) {
    OtherworldInnHudClient.winterColor.set(rgbColor);
  }

  public static int getDryColor() {
    return getOrDefault(dryColor);
  }

  public static void setDryColor(int rgbColor) {
    OtherworldInnHudClient.dryColor.set(rgbColor);
  }

  public static int getWetColor() {
    return getOrDefault(wetColor);
  }

  public static void setWetColor(int rgbColor) {
    OtherworldInnHudClient.wetColor.set(rgbColor);
  }

  // Season
  public static boolean getShowTropicalSeason() {
    return getOrDefault(showTropicalSeason);
  }

  public static void setShowTropicalSeason(boolean enable) {
    OtherworldInnHudClient.showTropicalSeason.set(enable);
  }

  public static boolean getShowSubSeason() {
    return getOrDefault(showSubSeason);
  }

  public static void setShowSubSeason(boolean enable) {
    OtherworldInnHudClient.showSubSeason.set(enable);
  }

  public static ShowDay getShowDay() {
    return getOrDefault(showDay);
  }

  public static void setShowDay(ShowDay showDay) {
    OtherworldInnHudClient.showDay.set(showDay);
  }

  public static boolean getShowFertility() {
    return getOrDefault(showFertility);
  }

  public static void setShowFertility(boolean showFertility) {
    OtherworldInnHudClient.showFertility.set(showFertility);
  }

  public static boolean getFertilityReplacesSeason() {
    return getOrDefault(fertilityReplacesSeason);
  }

  public static void setFertilityReplacesSeason(boolean fertilityReplacesSeason) {
    OtherworldInnHudClient.fertilityReplacesSeason.set(fertilityReplacesSeason);
  }

  public static boolean getShowDefaultWhenMinimapHidden() {
    return getOrDefault(showDefaultWhenMinimapHidden);
  }

  public static void setShowDefaultWhenMinimapHidden(boolean enable) {
    OtherworldInnHudClient.showDefaultWhenMinimapHidden.set(enable);
  }

  // Minimap
  public static boolean getEnableMinimapIntegration() {
    return getOrDefault(enableMinimapIntegration);
  }

  public static void setEnableMinimapIntegration(boolean enable) {
    OtherworldInnHudClient.enableMinimapIntegration.set(enable);
  }

  // Task HUD
  public static boolean getEnableTaskHud() {
    return getOrDefault(enableTaskHud);
  }

  public static void setEnableTaskHud(boolean enable) {
    OtherworldInnHudClient.enableTaskHud.set(enable);
  }

  public static int getTaskHudX() {
    return getOrDefault(taskHudX);
  }

  public static void setTaskHudX(int x) {
    OtherworldInnHudClient.taskHudX.set(x);
  }

  public static int getTaskHudY() {
    return getOrDefault(taskHudY);
  }

  public static void setTaskHudY(int y) {
    OtherworldInnHudClient.taskHudY.set(y);
  }

  public static double getTaskHudScale() {
    return getOrDefault(taskHudScale);
  }

  public static void setTaskHudScale(double scale) {
    OtherworldInnHudClient.taskHudScale.set(scale);
  }

  public static int getTaskHudMinimapReservedHeight() {
    return getOrDefault(taskHudMinimapReservedHeight);
  }

  public static void setTaskHudMinimapReservedHeight(int height) {
    OtherworldInnHudClient.taskHudMinimapReservedHeight.set(height);
  }

  public static int getTaskHudGapLines() {
    return getOrDefault(taskHudGapLines);
  }

  public static void setTaskHudGapLines(int gapLines) {
    OtherworldInnHudClient.taskHudGapLines.set(gapLines);
  }
}
