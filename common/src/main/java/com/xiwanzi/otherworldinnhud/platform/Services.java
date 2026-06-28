package com.xiwanzi.otherworldinnhud.platform;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.platform.services.AccessoryHelper;
import com.xiwanzi.otherworldinnhud.platform.services.MinimapHelper;
import com.xiwanzi.otherworldinnhud.platform.services.PlatformHelper;
import com.xiwanzi.otherworldinnhud.platform.services.SeasonHelper;
import java.util.ServiceLoader;

public class Services {
  public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
  public static final MinimapHelper MINIMAP = load(MinimapHelper.class);
  public static final SeasonHelper SEASON = load(SeasonHelper.class);
  public static final AccessoryHelper ACCESSORY = load(AccessoryHelper.class);

  private Services() {
  }

  public static <T> T load(Class<T> clazz) {
    ClassLoader serviceClassLoader = clazz.getClassLoader();
    final T loadedService = ServiceLoader.load(clazz, serviceClassLoader)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Failed to load service for " + clazz.getName() + " using " + serviceClassLoader));
    Common.LOG.debug("Loaded {} for service {}", loadedService, clazz);
    return loadedService;
  }
}
