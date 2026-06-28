package com.xiwanzi.otherworldinnhud.impl.season.mods.eclipticseasons;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.impl.season.components.Fertility;
import com.xiwanzi.otherworldinnhud.impl.season.components.Seasons;
import com.xiwanzi.otherworldinnhud.impl.season.components.SubSeasons;
import com.xiwanzi.otherworldinnhud.impl.season.mods.SeasonModHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class EclipticSeasonsHelper implements SeasonModHelper {

  @Override
  public Optional<Item> calendar() {
    if (Common.eclipticSeasonsLoaded()) {
      return Optional.of(
          BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("eclipticseasons", "calendar")));
    } else {
      return Optional.empty();
    }
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
    return Services.SEASON.currentEclipticSubSeason(player);
  }

  @Override
  public Seasons getCurrentSeason(Player player) {
    return Services.SEASON.currentEclipticSeason(player);
  }

  @Override
  public long getDate(Player player) {
    return Services.SEASON.currentEclipticSeasonDate(player);
  }

  @Override
  public int seasonDurationDays(Player player) {
    return Services.SEASON.currentEclipticSeasonDuration(player);
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
