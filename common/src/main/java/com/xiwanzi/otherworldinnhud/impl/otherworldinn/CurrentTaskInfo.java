package com.xiwanzi.otherworldinnhud.impl.otherworldinn;

import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.platform.Services;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CurrentTaskInfo {
  private static final String OTHERWORLD_INN_MOD_ID = "otherworldinn";
  private static final String[] API_CLASS_NAMES = {
      "com.otherworldinn.api.OtherworldInnHudSnapshotApi",
      "com.otherworldinn.client.hud.OtherworldInnHudSnapshotApi"
  };
  private static final int FORMAT_VERSION = 1;
  private static Accessor accessor;
  private static boolean integrationUnavailable;
  private static boolean failureLogged;

  private CurrentTaskInfo() {
  }

  public static List<TaskSnapshot> getTasks(Minecraft mc) {
    if (mc.level == null || mc.player == null || !Services.PLATFORM.isModLoaded(OTHERWORLD_INN_MOD_ID)
        || integrationUnavailable) {
      return List.of();
    }

    Optional<Accessor> currentAccessor = getAccessor();
    if (currentAccessor.isEmpty()) {
      return List.of();
    }

    try {
      return parseTasks(currentAccessor.get().getSnapshot(), mc);
    } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
      disableIntegration(e);
      return List.of();
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

  private static List<TaskSnapshot> parseTasks(CompoundTag root, Minecraft mc) {
    if (root.getInt("FormatVersion") != FORMAT_VERSION || !root.contains("Tasks", Tag.TAG_LIST)) {
      return List.of();
    }

    List<TaskSnapshot> tasks = new ArrayList<>();
    ListTag taskTags = root.getList("Tasks", Tag.TAG_COMPOUND);
    for (int i = 0; i < taskTags.size(); i++) {
      Tag taskTag = taskTags.get(i);
      if (taskTag instanceof CompoundTag compoundTask) {
        parseTask(compoundTask, mc, i).ifPresent(tasks::add);
      }
    }

    tasks.sort(Comparator.comparingLong(TaskSnapshot::acceptedAt).thenComparingInt(TaskSnapshot::snapshotIndex)
        .thenComparing(TaskSnapshot::id));
    return tasks.size() <= 2 ? tasks : List.copyOf(tasks.subList(0, 2));
  }

  private static Optional<TaskSnapshot> parseTask(CompoundTag tag, Minecraft mc, int snapshotIndex) {
    String id = tag.getString("Id");
    String kind = normalizeKind(tag.getString("Kind"));
    if (id.isBlank() || kind.isBlank()) {
      return Optional.empty();
    }

    List<RequirementSnapshot> requirements = parseRequirements(tag, mc);
    boolean requiresTurnIn = tag.getBoolean("RequiresTurnIn");
    boolean requirementsComplete = requirements.stream().allMatch(RequirementSnapshot::complete);
    boolean complete = tag.getBoolean("Complete") || (!requiresTurnIn && requirementsComplete);

    return Optional.of(new TaskSnapshot(
        id,
        kind,
        componentFromTag(tag, "TitleKey", "TitleText", defaultTaskTitle(kind)),
        requiresTurnIn,
        complete,
        tag.contains("AcceptedAt") ? tag.getLong("AcceptedAt") : Long.MAX_VALUE,
        snapshotIndex,
        requirements,
        parseRewards(tag)
    ));
  }

  private static List<RequirementSnapshot> parseRequirements(CompoundTag taskTag, Minecraft mc) {
    if (!taskTag.contains("Requirements", Tag.TAG_LIST)) {
      return List.of();
    }

    List<RequirementSnapshot> requirements = new ArrayList<>();
    ListTag requirementTags = taskTag.getList("Requirements", Tag.TAG_COMPOUND);
    for (Tag requirementTag : requirementTags) {
      if (requirementTag instanceof CompoundTag compoundRequirement) {
        requirements.add(parseRequirement(compoundRequirement, mc));
      }
    }
    return List.copyOf(requirements);
  }

  private static RequirementSnapshot parseRequirement(CompoundTag tag, Minecraft mc) {
    String id = tag.getString("Id");
    String type = tag.getString("Type");
    String targetId = tag.getString("TargetId");
    int required = Math.max(1, tag.getInt("Required"));
    CompoundTag nbt = tag.contains("Nbt", Tag.TAG_COMPOUND) ? tag.getCompound("Nbt").copy() : null;
    Component display = componentFromTag(tag, "DisplayKey", "DisplayText", defaultRequirementName(type, targetId));
    int current = tag.getInt("Current");
    boolean complete = tag.getBoolean("Complete");

    if ("item".equals(type)) {
      current = countInventoryItems(mc, targetId, nbt);
      complete = current >= required;
    } else if (current >= required) {
      complete = true;
    }

    return new RequirementSnapshot(id, type, targetId, display, required, Math.min(current, required), complete, nbt);
  }

  private static List<RewardSnapshot> parseRewards(CompoundTag taskTag) {
    if (!taskTag.contains("Rewards", Tag.TAG_LIST)) {
      return List.of();
    }

    List<RewardSnapshot> rewards = new ArrayList<>();
    ListTag rewardTags = taskTag.getList("Rewards", Tag.TAG_COMPOUND);
    for (Tag rewardTag : rewardTags) {
      if (rewardTag instanceof CompoundTag compoundReward) {
        String type = compoundReward.getString("Type");
        String targetId = compoundReward.getString("TargetId");
        int count = Math.max(0, compoundReward.getInt("Count"));
        rewards.add(new RewardSnapshot(
            type,
            targetId,
            componentFromTag(compoundReward, "DisplayKey", "DisplayText", defaultRewardName(type, targetId)),
            count
        ));
      }
    }
    return List.copyOf(rewards);
  }

  private static int countInventoryItems(Minecraft mc, String targetId, CompoundTag requiredNbt) {
    if (mc.player == null || mc.level == null) {
      return 0;
    }

    ResourceLocation itemId = ResourceLocation.tryParse(targetId);
    if (itemId == null) {
      return 0;
    }

    Item item = BuiltInRegistries.ITEM.get(itemId);
    Inventory inventory = mc.player.getInventory();
    int count = 0;
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (matchesRequirementStack(stack, item, requiredNbt, mc)) {
        count += stack.getCount();
      }
    }
    return count;
  }

  private static boolean matchesRequirementStack(
      ItemStack stack, Item item, CompoundTag requiredNbt, Minecraft mc) {
    if (stack.isEmpty() || !stack.is(item)) {
      return false;
    }
    if (requiredNbt == null || requiredNbt.isEmpty()) {
      return true;
    }
    if (mc.level == null) {
      return false;
    }
    Tag saved = stack.save(mc.level.registryAccess());
    return saved instanceof CompoundTag actual && nbtContains(actual, requiredNbt);
  }

  private static boolean nbtContains(Tag actual, Tag required) {
    if (required == null) {
      return true;
    }
    if (actual == null || actual.getId() != required.getId()) {
      return false;
    }
    if (required instanceof CompoundTag requiredCompound) {
      CompoundTag actualCompound = (CompoundTag) actual;
      for (String key : requiredCompound.getAllKeys()) {
        if (!actualCompound.contains(key) || !nbtContains(actualCompound.get(key), requiredCompound.get(key))) {
          return false;
        }
      }
      return true;
    }
    if (required instanceof ListTag requiredList) {
      ListTag actualList = (ListTag) actual;
      if (actualList.size() < requiredList.size()) {
        return false;
      }
      for (int i = 0; i < requiredList.size(); i++) {
        if (!nbtContains(actualList.get(i), requiredList.get(i))) {
          return false;
        }
      }
      return true;
    }
    return actual.equals(required);
  }

  private static Component componentFromTag(CompoundTag tag, String keyName, String textName, Component fallback) {
    String key = tag.getString(keyName);
    if (!key.isBlank()) {
      return Component.translatable(key);
    }

    String text = tag.getString(textName);
    if (!text.isBlank()) {
      return Common.literalText(text);
    }

    return fallback;
  }

  private static Component defaultTaskTitle(String kind) {
    return switch (kind) {
      case "story_wish" -> Common.translatedText("hud.otherworldinn_hud.task.story_wish");
      case "town_commission" -> Common.translatedText("hud.otherworldinn_hud.task.town_commission");
      default -> Common.literalText(kind);
    };
  }

  private static Component defaultRequirementName(String type, String targetId) {
    if ("item".equals(type)) {
      ResourceLocation itemId = ResourceLocation.tryParse(targetId);
      if (itemId != null) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return new ItemStack(item).getHoverName();
      }
    }
    if ("kill".equals(type)) {
      ResourceLocation entityId = ResourceLocation.tryParse(targetId);
      if (entityId != null) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        return entityType.getDescription();
      }
    }
    return targetId.isBlank() ? Common.literalText(type) : Common.literalText(targetId);
  }

  private static Component defaultRewardName(String type, String targetId) {
    if ("item".equals(type)) {
      return defaultRequirementName(type, targetId);
    }
    if ("coin".equals(type)) {
      return Common.translatedText("hud.otherworldinn_hud.task.reward.coins");
    }
    if ("favor".equals(type)) {
      return Common.translatedText("hud.otherworldinn_hud.task.reward.favor");
    }
    return targetId.isBlank() ? Common.literalText(type) : Common.literalText(targetId);
  }

  private static String normalizeKind(String kind) {
    return switch (kind) {
      case "story_guest" -> "story_wish";
      case "commission" -> "town_commission";
      default -> kind;
    };
  }

  private static Class<?> loadClass(String className) throws ClassNotFoundException {
    return Class.forName(className, false, CurrentTaskInfo.class.getClassLoader());
  }

  private static void disableIntegration(Throwable throwable) {
    integrationUnavailable = true;
    if (!failureLogged) {
      failureLogged = true;
      Common.LOG.warn("Disabling OtherworldInn task HUD integration: {}", throwable.toString());
      Common.LOG.debug("OtherworldInn task HUD integration failure", throwable);
    }
  }

  public record TaskSnapshot(
      String id,
      String kind,
      Component title,
      boolean requiresTurnIn,
      boolean complete,
      long acceptedAt,
      int snapshotIndex,
      List<RequirementSnapshot> requirements,
      List<RewardSnapshot> rewards
  ) {
  }

  public record RequirementSnapshot(
      String id,
      String type,
      String targetId,
      Component display,
      int required,
      int current,
      boolean complete,
      CompoundTag nbt
  ) {
  }

  public record RewardSnapshot(String type, String targetId, Component display, int count) {
  }

  private record Accessor(Method getTaskHudSnapshot) {
    private static Accessor create() throws ReflectiveOperationException {
      ReflectiveOperationException failure = null;
      for (String apiClassName : API_CLASS_NAMES) {
        try {
          Class<?> apiClass = loadClass(apiClassName);
          return new Accessor(apiClass.getMethod("getTaskHudSnapshot"));
        } catch (ReflectiveOperationException e) {
          failure = e;
        }
      }
      throw failure == null ? new ClassNotFoundException("OtherworldInn task HUD API") : failure;
    }

    private CompoundTag getSnapshot() throws ReflectiveOperationException {
      Object result = getTaskHudSnapshot.invoke(null);
      return result instanceof CompoundTag snapshot ? snapshot.copy() : new CompoundTag();
    }
  }
}
