package com.xiwanzi.otherworldinnhud.impl.season.mods;

import com.xiwanzi.otherworldinnhud.impl.season.mods.eclipticseasons.EclipticSeasonsHelper;
import com.xiwanzi.otherworldinnhud.impl.season.mods.fabricseasons.FabricSeasonsHelper;
import com.xiwanzi.otherworldinnhud.impl.season.mods.homeostaticseasons.HomeostaticSeasonsHelper;
import com.xiwanzi.otherworldinnhud.impl.season.mods.protomanlyweather.ProtoManlyWeatherHelper;
import com.xiwanzi.otherworldinnhud.impl.season.mods.sereneseasons.SereneSeasonsHelper;
import com.xiwanzi.otherworldinnhud.impl.season.mods.terrafirmacraft.TerrafirmaCraftHelper;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.util.ArrayList;
import java.util.List;

public enum SeasonMods {
  SERENE("sereneseasons", new SereneSeasonsHelper()),

  FABRIC("seasons", new FabricSeasonsHelper()),

  FABRIC_EXTRAS("seasonsextras", new FabricSeasonsHelper()),

  TERRAFIRMACRAFT("tfc", new TerrafirmaCraftHelper()),

  ECLIPTIC("eclipticseasons", new EclipticSeasonsHelper()),

  HOMEOSTATIC("homeostaticseasons", new HomeostaticSeasonsHelper()),

  PROTOMANLY_WEATHER("pmweather", new ProtoManlyWeatherHelper());

  private final String modId;
  private final SeasonModHelper seasonModHelper;

  SeasonMods(String modId, SeasonModHelper seasonModHelper) {
    this.modId = modId;
    this.seasonModHelper = seasonModHelper;
  }

  public static List<SeasonMods> getLoaded() {
    List<SeasonMods> values = new ArrayList<>(List.of(SeasonMods.values()));
    List<SeasonMods> loaded = new ArrayList<>();

    values.forEach(seasonMod -> {
      if (seasonMod.modLoaded()) {
        loaded.add(seasonMod);
      }
    });
    return loaded;
  }

  public String getModId() {
    return this.modId;
  }

  public String getModName() {
    return Services.PLATFORM.getModName(this.getModId());
  }

  public boolean modLoaded() {
    return Services.PLATFORM.isModLoaded(this.getModId());
  }

  public SeasonModHelper getSeasonModHelper() {
    return seasonModHelper;
  }
}
