package com.xiwanzi.otherworldinnhud.impl.otherworldinn;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class CurrentRoomInfo {
  private static final String OTHERWORLD_INN_MOD_ID = "otherworldinn";
  private static final String COMFORT_ICON = "\uE002";
  private static final String LIGHT_ICON = "\uE003";
  private static final String HUMIDITY_ICON = "\uE004";
  private static final int COMFORT_COLOR = 0xFF6A6A;
  private static final int LIGHT_COLOR = 0xFFD700;
  private static final int HUMIDITY_COLOR = 0x97FFFF;
  private static final int LABEL_COLOR = 0xC0C0C0;
  private static final int VALUE_COLOR = 0xFFFFFF;
  private static final int WARNING_COLOR = 0xFF6A6A;
  private static Accessor accessor;
  private static boolean integrationUnavailable;
  private static boolean failureLogged;

  private CurrentRoomInfo() {
  }

  public static Optional<MutableComponent> getHudText(Minecraft mc) {
    if (mc.player == null || !Services.PLATFORM.isModLoaded(OTHERWORLD_INN_MOD_ID) || integrationUnavailable) {
      return Optional.empty();
    }

    Optional<Accessor> currentAccessor = getAccessor();
    if (currentAccessor.isEmpty()) {
      return Optional.empty();
    }

    try {
      RoomSnapshot room = currentAccessor.get().findCurrentRoom(mc.player.blockPosition());
      return room == null ? Optional.empty() : Optional.of(formatRoom(room));
    } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
      disableIntegration(e);
      return Optional.empty();
    }
  }

  private static Optional<Accessor> getAccessor() {
    if (accessor != null) {
      return Optional.of(accessor);
    }

    try {
      accessor = Accessor.create();
      return Optional.of(accessor);
    } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
      disableIntegration(e);
      return Optional.empty();
    }
  }

  private static MutableComponent formatRoom(RoomSnapshot room) {
    MutableComponent line = Common.literalText("");
    boolean hasContent = false;

    hasContent = appendMetric(line, hasContent, COMFORT_ICON, room.comfort(), COMFORT_COLOR);
    hasContent = appendMetric(line, hasContent, LIGHT_ICON, room.light(), LIGHT_COLOR);
    hasContent = appendMetric(line, hasContent, HUMIDITY_ICON, room.humidity(), HUMIDITY_COLOR);

    if (room.cleanliness() < 100) {
      appendSeparator(line, hasContent);
      hasContent = true;
      line.append(Common.literalText("净 ").withStyle(Style.EMPTY.withColor(LABEL_COLOR)));
      line.append(Common.literalText(room.cleanliness() + "%").withStyle(Style.EMPTY.withColor(WARNING_COLOR)));
    }

    if (room.currentGuests() > 0 || room.totalBeds() != 1) {
      appendSeparator(line, hasContent);
      line.append(Common.literalText("床 ").withStyle(Style.EMPTY.withColor(LABEL_COLOR)));
      line.append(Common.literalText(room.currentGuests() + "/" + room.totalBeds())
                     .withStyle(Style.EMPTY.withColor(VALUE_COLOR)));
    }

    return line;
  }

  private static boolean appendMetric(MutableComponent line, boolean hasContent, String icon, int value, int color) {
    appendSeparator(line, hasContent);
    line.append(Common.literalText(icon + " ").withStyle(Style.EMPTY.withColor(color)));
    line.append(Common.literalText(String.valueOf(value)).withStyle(Style.EMPTY.withColor(color)));
    return true;
  }

  private static void appendSeparator(MutableComponent line, boolean hasContent) {
    if (hasContent) {
      line.append(Common.literalText("  "));
    }
  }

  private static Class<?> loadClass(String className) throws ClassNotFoundException {
    return Class.forName(className, false, CurrentRoomInfo.class.getClassLoader());
  }

  private static void disableIntegration(Throwable throwable) {
    integrationUnavailable = true;
    if (!failureLogged) {
      failureLogged = true;
      Common.LOG.warn("Disabling OtherworldInn room HUD integration: {}", throwable.toString());
      Common.LOG.debug("OtherworldInn room HUD integration failure", throwable);
    }
  }

  private record RoomSnapshot(int comfort, int light, int humidity, int cleanliness, int currentGuests, int totalBeds) {
  }

  private record Accessor(
      Method getTeamManagerInstance,
      Method getClientPlayerTeam,
      Method getInnData,
      Method getRoomAt,
      Method getComfort,
      Method getLight,
      Method getHumidity,
      Method getCleanliness,
      Method getCurrentGuests,
      Method getTotalBeds
  ) {
    private static Accessor create() throws ReflectiveOperationException {
      Class<?> teamManagerClass = loadClass("com.otherworldinn.world.team.service.TeamManager");
      Class<?> teamDataClass = loadClass("com.otherworldinn.world.team.TeamData");
      Class<?> innDataClass = loadClass("com.otherworldinn.world.inn.InnData");
      Class<?> roomDataClass = loadClass("com.otherworldinn.world.inn.RoomData");

      return new Accessor(
          teamManagerClass.getMethod("getInstance"),
          teamManagerClass.getMethod("getClientPlayerTeam"),
          teamDataClass.getMethod("getInnData"),
          innDataClass.getMethod("getRoomAt", BlockPos.class),
          roomDataClass.getMethod("getComfort"),
          roomDataClass.getMethod("getLight"),
          roomDataClass.getMethod("getHumidity"),
          roomDataClass.getMethod("getCleanliness"),
          roomDataClass.getMethod("getCurrentGuests"),
          roomDataClass.getMethod("getTotalBeds")
      );
    }

    private RoomSnapshot findCurrentRoom(BlockPos playerPos) throws ReflectiveOperationException {
      Object teamManager = getTeamManagerInstance.invoke(null);
      Object team = getClientPlayerTeam.invoke(teamManager);
      if (team == null) {
        return null;
      }

      Object innData = getInnData.invoke(team);
      if (innData == null) {
        return null;
      }

      Object room = getRoomAt.invoke(innData, playerPos);
      if (room == null) {
        return null;
      }

      Object currentGuests = getCurrentGuests.invoke(room);
      int currentGuestCount = currentGuests instanceof Collection<?> guests ? guests.size() : 0;

      return new RoomSnapshot(
          (Integer) getComfort.invoke(room),
          (Integer) getLight.invoke(room),
          (Integer) getHumidity.invoke(room),
          (Integer) getCleanliness.invoke(room),
          currentGuestCount,
          (Integer) getTotalBeds.invoke(room)
      );
    }
  }
}
