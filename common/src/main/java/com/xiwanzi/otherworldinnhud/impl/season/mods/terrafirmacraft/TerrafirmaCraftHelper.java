package com.xiwanzi.otherworldinnhud.impl.season.mods.terrafirmacraft;

import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.season.components.Fertility;
import com.xiwanzi.otherworldinnhud.impl.season.components.Months;
import com.xiwanzi.otherworldinnhud.impl.season.components.Seasons;
import com.xiwanzi.otherworldinnhud.impl.season.components.SubSeasons;
import com.xiwanzi.otherworldinnhud.impl.season.mods.SeasonModHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class TerrafirmaCraftHelper implements SeasonModHelper {
  public TerrafirmaCraftHelper() {
  }

  @Override
  public Optional<Item> calendar() {
    return Optional.empty();
  }

  @Override
  public boolean isTropicalSeason(Player player) {
    return false;
  }

  @Override
  public boolean isSeasonTiedWithSystemTime() {
    return false;
  }

  @Override
  public SubSeasons getCurrentSubSeason(Player player) {
    return Services.SEASON.currentTerraFirmaCraftMonth().getSubSeason();
  }

  @Override
  public Seasons getCurrentSeason(Player player) {
    return Services.SEASON.currentTerraFirmaCraftMonth().getSeason();
  }

  @Override
  public long getDate(Player player) {
    Months currentMonth = Services.SEASON.currentTerraFirmaCraftMonth();
    SubSeasons currentSubSeason = currentMonth.getSubSeason();

    int dayOfMonth = Services.SEASON.terraFirmaCraftCurrentDayOfMonth();
    int daysInMonth = Services.SEASON.terraFirmaCraftTotalDaysInMonth();

    // Assumes that there are 3 months per season
    if (OtherworldInnHudClient.getShowSubSeason() && Calendar.validDetailedMode(player)) {
      return dayOfMonth;
    } else {
      // Early = 0; Mid = 1; Late = 2
      return dayOfMonth + ((long) currentSubSeason.ordinal() * daysInMonth);
    }
  }

  @Override
  public int seasonDurationDays(Player player) {
    int daysInMonth = Services.SEASON.terraFirmaCraftTotalDaysInMonth();

    // Currently the days in a month is 8 by default, and determined by the 'yearLength' config value divided by 12
    if (OtherworldInnHudClient.getShowSubSeason() && Calendar.validDetailedMode(player)) {
      return daysInMonth;
    } else {
      return daysInMonth * 3;
    }
  }

  @Override
  public boolean infertileBiome(Player player) {
    return false;
  }

  @Override
  public boolean alwaysWinterBiome(Player player) {
    return false;
  }

  @Override
  public boolean undergroundFertile(Player player) {
    return true;
  }

  @Override
  public Fertility fertility(Player player) {
    return SeasonModHelper.super.fertility(player);
  }

  @Override
  public void debugHud(GuiGraphics graphics) {

  }

}