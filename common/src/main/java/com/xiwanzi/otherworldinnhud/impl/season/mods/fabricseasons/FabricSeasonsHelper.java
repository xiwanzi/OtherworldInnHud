package com.xiwanzi.otherworldinnhud.impl.season.mods.fabricseasons;

import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudServer;
import com.xiwanzi.otherworldinnhud.impl.accessory.mods.Calendar;
import com.xiwanzi.otherworldinnhud.impl.season.components.Fertility;
import com.xiwanzi.otherworldinnhud.impl.season.components.Seasons;
import com.xiwanzi.otherworldinnhud.impl.season.components.SubSeasons;
import com.xiwanzi.otherworldinnhud.impl.season.mods.SeasonModHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.time.LocalDateTime;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class FabricSeasonsHelper implements SeasonModHelper {
  public FabricSeasonsHelper() {
  }

  @Override
  public Optional<Item> calendar() {
    return Services.SEASON.fabricSeasonsCalendar();
  }

  @Override
  public boolean isTropicalSeason(Player player) {
    return false;
  }

  @Override
  public boolean isSeasonTiedWithSystemTime() {
    return Services.SEASON.fabricSeasonsTiedWithSystemTime();
  }

  @Override
  public SubSeasons getCurrentSubSeason(Player player) {
    return Services.SEASON.currentFabricSubSeason(player);
  }

  @Override
  public Seasons getCurrentSeason(Player player) {
    return Services.SEASON.currentFabricSeason(player);
  }

  @Override
  public long getDate(Player player) {
    long dayLengthTick = OtherworldInnHudServer.getDayLength();
    long seasonLengthTick = Services.SEASON.currentFabricSeasonLength(player);
    long seasonLengthDay = seasonLengthTick / dayLengthTick; // Current season length (Default 28)
    long subSeasonLengthDay = seasonLengthDay / 3;
    long timeToNextSeason = Services.SEASON.timeToNextFabricSeason(player);
    // long seasonDay = ((seasonLength - timeToNextSeason) / dayLength) + 1;
    long seasonDay = (seasonLengthTick - timeToNextSeason) / dayLengthTick;
    long seasonDate = (seasonDay % seasonLengthDay) + 1; // Default 28 days in a season
    long subSeasonDate = (seasonDay % subSeasonLengthDay) + 1; // Default ~9 days

    // Get the current day of month from the system. Used with fabric seasons' system time tied with season option
    if (isSeasonTiedWithSystemTime()) {
      return LocalDateTime.now().getDayOfMonth();
    }
    if (OtherworldInnHudClient.getShowSubSeason() && Calendar.validDetailedMode(player)) {
      return subSeasonDate;
    } else {
      return seasonDate;
    }
  }

  @Override
  public int seasonDurationDays(Player player) {
    int dayLength = OtherworldInnHudServer.getDayLength();
    int seasonLength = Services.SEASON.currentFabricSeasonLength(player);
    int duration = seasonLength / dayLength;

    if (OtherworldInnHudClient.getShowSubSeason() && Calendar.validDetailedMode(player)) {
      duration /= 3;
    }

    return duration;
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
