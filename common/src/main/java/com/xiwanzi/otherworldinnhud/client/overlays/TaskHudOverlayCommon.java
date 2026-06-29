package com.xiwanzi.otherworldinnhud.client.overlays;

import com.mojang.blaze3d.platform.InputConstants;
import com.xiwanzi.otherworldinnhud.Common;
import com.xiwanzi.otherworldinnhud.client.gui.TaskHudLocation;
import com.xiwanzi.otherworldinnhud.client.gui.TaskHudStyle;
import com.xiwanzi.otherworldinnhud.config.OtherworldInnHudClient;
import com.xiwanzi.otherworldinnhud.impl.minimap.CurrentMinimap;
import com.xiwanzi.otherworldinnhud.impl.otherworldinn.CurrentTaskInfo;
import com.xiwanzi.otherworldinnhud.impl.otherworldinn.CurrentTaskInfo.RequirementSnapshot;
import com.xiwanzi.otherworldinnhud.impl.otherworldinn.CurrentTaskInfo.RewardSnapshot;
import com.xiwanzi.otherworldinnhud.impl.otherworldinn.CurrentTaskInfo.TaskSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

public final class TaskHudOverlayCommon {
  private static final ResourceLocation STORY_TITLE_TEXTURE =
      Common.location("textures/gui/task_hud/story_wish_title.png");
  private static final ResourceLocation TOWN_TITLE_TEXTURE =
      Common.location("textures/gui/task_hud/town_commission_title.png");
  private static final int TEXT_WIDTH = 172;
  private static final int TITLE_WIDTH = 116;
  private static final int TITLE_HEIGHT = 20;
  private static final int BODY_FRAME = 4;
  private static final int BODY_TEXT_PADDING_X = 5;
  private static final int BODY_VISUAL_WIDTH = TEXT_WIDTH + (BODY_TEXT_PADDING_X * 2) + (BODY_FRAME * 2);
  private static final int BODY_TOP_PADDING = 2;
  private static final int BODY_BOTTOM_PADDING = 5;
  private static final int TITLE_TO_BODY_GAP = 3;
  private static final int SECTION_GAP = 3;
  private static final int CENTER_COLUMN_GAP = 16;
  private static final int CENTER_Y_OFFSET = -28;
  private static final int TASK_TITLE_COLOR = 0xF8E6B0;
  private static final int REQUIREMENT_OPEN_COLOR = 0xD9C9A7;
  private static final int REQUIREMENT_COMPLETE_COLOR = 0x8DE08A;
  private static final int REWARD_COLOR = 0xFFD36D;
  private static final int PURE_COMPLETE_HOLD_MS = 5000;
  private static final int FADE_MS = 500;
  private static final Map<String, TaskRenderState> STATES = new HashMap<>();
  private static final Set<String> SUPPRESSED_COMPLETED_TASKS = new HashSet<>();

  private TaskHudOverlayCommon() {
  }

  public static void render(GuiGraphics graphics, Minecraft mc) {
    if (!shouldDraw(mc)) {
      if (mc.player == null || mc.level == null) {
        STATES.clear();
      }
      return;
    }

    long now = Util.getMillis();
    updateStates(CurrentTaskInfo.getTasks(mc), now);
    List<RenderTask> renderTasks = getRenderTasks(now);
    if (renderTasks.isEmpty()) {
      return;
    }

    double scale = OtherworldInnHudClient.getTaskHudScale();
    TaskHudLocation location = OtherworldInnHudClient.getTaskHudLocation();
    TaskHudStyle style = TaskHudStyle.CLEAN_CLOTH;
    int screenWidth = mc.getWindow().getGuiScaledWidth();
    int screenHeight = mc.getWindow().getGuiScaledHeight();
    int gap = (mc.font.lineHeight * OtherworldInnHudClient.getTaskHudGapLines()) + 4;
    List<Integer> blockHeights = measureBlockHeights(mc.font, renderTasks);

    graphics.pose().pushPose();
    graphics.pose().scale((float) scale, (float) scale, 1F);
    if (location == TaskHudLocation.CENTER) {
      renderCenteredTasks(graphics, mc, renderTasks, blockHeights, screenWidth, screenHeight, scale, style);
    } else {
      renderRightSideTasks(graphics, mc, renderTasks, blockHeights, screenWidth, screenHeight, scale, gap);
    }
    graphics.pose().popPose();
  }

  private static boolean shouldDraw(Minecraft mc) {
    return OtherworldInnHudClient.getEnableMod() && OtherworldInnHudClient.getEnableTaskHud()
        && isTaskHudKeyDown(mc) && Common.vanillaShouldDrawHud(mc);
  }

  public static boolean shouldHideCrosshair(Minecraft mc) {
    return shouldDraw(mc);
  }

  private static boolean isTaskHudKeyDown(Minecraft mc) {
    return mc.getWindow() != null
        && InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_TAB);
  }

  private static void renderCenteredTasks(
      GuiGraphics graphics,
      Minecraft mc,
      List<RenderTask> renderTasks,
      List<Integer> blockHeights,
      int screenWidth,
      int screenHeight,
      double scale,
      TaskHudStyle style) {
    int count = renderTasks.size();
    int blockHeight = maxHeight(blockHeights);
    int totalWidth = (BODY_VISUAL_WIDTH * count) + (CENTER_COLUMN_GAP * Math.max(0, count - 1));
    int x = Math.max(0, (int) (((screenWidth / scale) - totalWidth) / 2D));
    int y = Math.max(0, (int) (((screenHeight / scale) - blockHeight) / 2D) + CENTER_Y_OFFSET);

    for (int i = 0; i < count; i++) {
      int blockX = x + (i * (BODY_VISUAL_WIDTH + CENTER_COLUMN_GAP));
      renderTask(graphics, mc, renderTasks.get(i), blockX, y, blockHeight, true, style);
    }
  }

  private static void renderRightSideTasks(
      GuiGraphics graphics,
      Minecraft mc,
      List<RenderTask> renderTasks,
      List<Integer> blockHeights,
      int screenWidth,
      int screenHeight,
      double scale,
      int gap) {
    int totalHeight = totalHeight(blockHeights, gap);
    int x = taskHudX(screenWidth, scale);
    int y = taskHudY(mc, screenHeight, scale, totalHeight);

    int blockY = y;
    for (int i = 0; i < renderTasks.size(); i++) {
      int blockHeight = renderTask(
          graphics, mc, renderTasks.get(i), x, blockY, blockHeights.get(i), true, TaskHudStyle.CLEAN_CLOTH);
      blockY += blockHeight + gap;
    }
  }

  private static int taskHudX(int screenWidth, double scale) {
    int rightMargin = OtherworldInnHudClient.getTaskHudX();
    return (int) ((screenWidth - rightMargin - Math.ceil(BODY_VISUAL_WIDTH * scale)) / scale);
  }

  private static int taskHudY(Minecraft mc, int screenHeight, double scale, int totalHeight) {
    int y = (int) (((screenHeight / scale) - totalHeight) / 2D) + OtherworldInnHudClient.getTaskHudY();
    int topReserve = 0;
    if (CurrentMinimap.hasVisibleMinimap(mc)) {
      topReserve = (int) Math.ceil(OtherworldInnHudClient.getTaskHudMinimapReservedHeight() / scale);
    }
    return Math.max(y, topReserve);
  }

  private static int maxHeight(List<Integer> heights) {
    int max = 0;
    for (int height : heights) {
      max = Math.max(max, height);
    }
    return max;
  }

  private static List<Integer> measureBlockHeights(Font font, List<RenderTask> renderTasks) {
    List<Integer> heights = new ArrayList<>(renderTasks.size());
    for (RenderTask renderTask : renderTasks) {
      heights.add(measureTaskHeight(font, renderTask.task));
    }
    return heights;
  }

  private static int totalHeight(List<Integer> heights, int gap) {
    int total = 0;
    for (int height : heights) {
      total += height;
    }
    if (!heights.isEmpty()) {
      total += gap * (heights.size() - 1);
    }
    return total;
  }

  private static void updateStates(List<TaskSnapshot> tasks, long now) {
    Set<String> presentIds = new HashSet<>();
    for (TaskSnapshot task : tasks) {
      presentIds.add(task.id());
      if (SUPPRESSED_COMPLETED_TASKS.contains(task.id()) && task.complete()) {
        continue;
      }
      SUPPRESSED_COMPLETED_TASKS.remove(task.id());
      TaskRenderState state = STATES.computeIfAbsent(task.id(), ignored -> new TaskRenderState(task, now));
      state.update(task, now);
    }

    SUPPRESSED_COMPLETED_TASKS.removeIf(taskId -> !presentIds.contains(taskId));

    for (TaskRenderState state : STATES.values()) {
      if (!presentIds.contains(state.task.id())) {
        state.markMissing(now);
      }
    }
  }

  private static List<RenderTask> getRenderTasks(long now) {
    List<RenderTask> renderTasks = new ArrayList<>();
    Iterator<Map.Entry<String, TaskRenderState>> iterator = STATES.entrySet().iterator();
    while (iterator.hasNext()) {
      TaskRenderState state = iterator.next().getValue();
      float alpha = state.alpha(now);
      if (alpha <= 0F) {
        if (state.task.complete()) {
          SUPPRESSED_COMPLETED_TASKS.add(state.task.id());
        }
        iterator.remove();
        continue;
      }
      renderTasks.add(new RenderTask(state.task, alpha));
    }

    renderTasks.sort((left, right) -> {
      int accepted = Long.compare(left.task.acceptedAt(), right.task.acceptedAt());
      if (accepted != 0) {
        return accepted;
      }
      int snapshotOrder = Integer.compare(left.task.snapshotIndex(), right.task.snapshotIndex());
      return snapshotOrder != 0 ? snapshotOrder : left.task.id().compareTo(right.task.id());
    });
    return renderTasks.size() <= 2 ? renderTasks : List.copyOf(renderTasks.subList(0, 2));
  }

  private static int renderTask(
      GuiGraphics graphics,
      Minecraft mc,
      RenderTask renderTask,
      int x,
      int y,
      int blockHeight,
      boolean bodyPanel,
      TaskHudStyle style) {
    TaskSnapshot task = renderTask.task;
    float alpha = renderTask.alpha;
    Font font = mc.font;
    BodyPalette palette = bodyPalette(style);
    if (bodyPanel) {
      renderBodyBackground(graphics, x, y, blockHeight, alpha, palette);
    }
    int titleX = x + ((BODY_VISUAL_WIDTH - TITLE_WIDTH) / 2);
    renderTitle(graphics, mc, task, titleX, y, alpha);

    int textX = x + BODY_FRAME + BODY_TEXT_PADDING_X;
    int lineY = y + TITLE_HEIGHT + TITLE_TO_BODY_GAP + BODY_TOP_PADDING;
    int taskTitleColor = bodyPanel ? palette.taskTitleText : TASK_TITLE_COLOR;
    int requirementOpenColor = bodyPanel ? palette.requirementText : REQUIREMENT_OPEN_COLOR;
    int requirementCompleteColor = bodyPanel ? palette.completeText : REQUIREMENT_COMPLETE_COLOR;
    int rewardColor = bodyPanel ? palette.rewardText : REWARD_COLOR;
    boolean textShadow = !bodyPanel;
    if (task.guestName() != null) {
      lineY = drawWrapped(graphics, font, formatGuestName(task.guestName()), textX, lineY, taskTitleColor, alpha,
                          textShadow);
      lineY += SECTION_GAP;
    }

    lineY = drawWrapped(graphics, font, task.title(), textX, lineY, taskTitleColor, alpha, textShadow);

    if (!task.requirements().isEmpty()) {
      lineY += SECTION_GAP;
    }

    for (RequirementSnapshot requirement : task.requirements()) {
      MutableComponent line = formatRequirement(requirement);
      int color = requirement.complete() ? requirementCompleteColor : requirementOpenColor;
      lineY = drawWrapped(graphics, font, line, textX, lineY, color, alpha, textShadow);
    }

    if (!task.rewards().isEmpty()) {
      lineY += SECTION_GAP;
      lineY = drawWrapped(graphics, font, formatRewards(task.rewards()), textX, lineY, rewardColor, alpha, textShadow);
    }

    return Math.max(blockHeight, Math.max(TITLE_HEIGHT, lineY - y));
  }

  private static void renderBodyBackground(
      GuiGraphics graphics, int x, int y, int blockHeight, float alpha, BodyPalette palette) {
    int backgroundX = x;
    int backgroundY = y + TITLE_HEIGHT;
    int backgroundWidth = BODY_VISUAL_WIDTH;
    int backgroundHeight = Math.max(10, blockHeight - TITLE_HEIGHT);

    graphics.fill(backgroundX, backgroundY, backgroundX + backgroundWidth, backgroundY + backgroundHeight,
                  withAlpha(palette.outerWood, alpha * 0.94F));
    graphics.fill(backgroundX + 1, backgroundY + 1, backgroundX + backgroundWidth - 1,
                  backgroundY + backgroundHeight - 1, withAlpha(palette.innerWood, alpha * 0.92F));
    graphics.fill(backgroundX + 2, backgroundY + 2, backgroundX + backgroundWidth - 2,
                  backgroundY + backgroundHeight - 2, withAlpha(palette.woodShadow, alpha * 0.88F));
    graphics.fill(backgroundX + BODY_FRAME, backgroundY + BODY_FRAME,
                  backgroundX + backgroundWidth - BODY_FRAME, backgroundY + backgroundHeight - BODY_FRAME,
                  withAlpha(palette.cloth, alpha * 0.92F));

    int clothX = backgroundX + BODY_FRAME;
    int clothY = backgroundY + BODY_FRAME;
    int clothWidth = backgroundWidth - (BODY_FRAME * 2);
    int clothHeight = backgroundHeight - (BODY_FRAME * 2);
    graphics.fill(clothX, clothY, clothX + clothWidth, clothY + 1,
                  withAlpha(palette.clothLight, alpha * 0.38F));
    graphics.fill(clothX, clothY + clothHeight - 1, clothX + clothWidth, clothY + clothHeight,
                  withAlpha(palette.clothShadow, alpha * 0.26F));
    graphics.fill(clothX, clothY, clothX + 1, clothY + clothHeight,
                  withAlpha(palette.clothLight, alpha * 0.24F));
    graphics.fill(clothX + clothWidth - 1, clothY, clothX + clothWidth, clothY + clothHeight,
                  withAlpha(palette.clothShadow, alpha * 0.24F));

    for (int lineY = clothY + 5; lineY < clothY + clothHeight - 3; lineY += palette.threadSpacing) {
      graphics.fill(clothX + 4, lineY, clothX + clothWidth - 4, lineY + 1,
                    withAlpha(palette.thread, alpha * palette.threadAlpha));
    }
    for (int lineX = clothX + 10; lineX < clothX + clothWidth - 8; lineX += palette.threadSpacing * 3) {
      graphics.fill(lineX, clothY + 3, lineX + 1, clothY + clothHeight - 3,
                    withAlpha(palette.thread, alpha * palette.threadAlpha * 0.55F));
    }

    graphics.fill(backgroundX + 3, backgroundY + 3, backgroundX + 14, backgroundY + 4,
                  withAlpha(palette.woodHighlight, alpha * 0.58F));
    graphics.fill(backgroundX + 3, backgroundY + 3, backgroundX + 4, backgroundY + 11,
                  withAlpha(palette.woodHighlight, alpha * 0.48F));
    graphics.fill(backgroundX + backgroundWidth - 14, backgroundY + backgroundHeight - 4,
                  backgroundX + backgroundWidth - 3, backgroundY + backgroundHeight - 3,
                  withAlpha(palette.woodShadow, alpha * 0.72F));
    graphics.fill(backgroundX + backgroundWidth - 4, backgroundY + backgroundHeight - 11,
                  backgroundX + backgroundWidth - 3, backgroundY + backgroundHeight - 3,
                  withAlpha(palette.woodShadow, alpha * 0.62F));
  }

  private static int measureTaskHeight(Font font, TaskSnapshot task) {
    int height = TITLE_HEIGHT + TITLE_TO_BODY_GAP + BODY_TOP_PADDING;
    if (task.guestName() != null) {
      height += wrappedHeight(font, formatGuestName(task.guestName()));
      height += SECTION_GAP;
    }

    height += wrappedHeight(font, task.title());

    if (!task.requirements().isEmpty()) {
      height += SECTION_GAP;
    }
    for (RequirementSnapshot requirement : task.requirements()) {
      height += wrappedHeight(font, formatRequirement(requirement));
    }

    if (!task.rewards().isEmpty()) {
      height += SECTION_GAP;
      height += wrappedHeight(font, formatRewards(task.rewards()));
    }
    height += BODY_BOTTOM_PADDING;

    return Math.max(TITLE_HEIGHT, height);
  }

  private static int wrappedHeight(Font font, Component text) {
    return font.split(text, TEXT_WIDTH).size() * font.lineHeight;
  }

  private static void renderTitle(GuiGraphics graphics, Minecraft mc, TaskSnapshot task, int x, int y, float alpha) {
    ResourceLocation texture = "story_wish".equals(task.kind()) ? STORY_TITLE_TEXTURE : TOWN_TITLE_TEXTURE;
    if (mc.getResourceManager().getResource(texture).isPresent()) {
      graphics.setColor(1F, 1F, 1F, alpha);
      graphics.blit(texture, x, y, 0, 0, TITLE_WIDTH, TITLE_HEIGHT, TITLE_WIDTH, TITLE_HEIGHT);
      graphics.setColor(1F, 1F, 1F, 1F);
    }

    Component fallback = "story_wish".equals(task.kind())
                         ? Common.translatedText("hud.otherworldinn_hud.task.story_wish")
                         : Common.translatedText("hud.otherworldinn_hud.task.town_commission");
    int textX = x + (TITLE_WIDTH - mc.font.width(fallback)) / 2;
    int textY = y + (TITLE_HEIGHT - mc.font.lineHeight) / 2;
    int titleColor = "story_wish".equals(task.kind()) ? 0xFFE36A : 0xF4EAD7;
    graphics.drawString(mc.font, fallback, textX, textY, withAlpha(titleColor, alpha), true);
  }

  private static int drawWrapped(
      GuiGraphics graphics, Font font, Component text, int x, int y, int color, float alpha, boolean shadow) {
    List<FormattedCharSequence> lines = font.split(text, TEXT_WIDTH);
    for (FormattedCharSequence line : lines) {
      graphics.drawString(font, line, x, y, withAlpha(color, alpha), shadow);
      y += font.lineHeight;
    }
    return y;
  }

  private static MutableComponent formatGuestName(Component guestName) {
    return guestName.copy();
  }

  private static MutableComponent formatRewards(List<RewardSnapshot> rewards) {
    MutableComponent line = Common.translatedText("hud.otherworldinn_hud.task.rewards").append(Common.literalText(" "));
    for (int i = 0; i < rewards.size(); i++) {
      RewardSnapshot reward = rewards.get(i);
      if (i > 0) {
        line.append(Common.literalText(", "));
      }
      if ("npc_favor".equals(reward.type()) || "favor".equals(reward.type())) {
        line.append(reward.display())
            .append(Common.literalText(" "))
            .append(Common.translatedText("hud.otherworldinn_hud.task.reward.favor"));
      } else {
        line.append(reward.display());
      }
      if (reward.count() > 1 || "npc_favor".equals(reward.type()) || "favor".equals(reward.type())) {
        line.append(Common.literalText(" x" + reward.count()));
      }
    }
    return line;
  }

  private static MutableComponent formatRequirement(RequirementSnapshot requirement) {
    MutableComponent line = Common.literalText(requirement.complete() ? "[x] " : "[ ] ");
    line.append(requirement.display());
    if (requirement.required() > 1 || requirement.current() > 0) {
      line.append(Common.literalText(" " + requirement.current() + "/" + requirement.required()));
    }
    return line;
  }

  private static BodyPalette bodyPalette(TaskHudStyle style) {
    return switch (style) {
      case WARM_CLOTH -> new BodyPalette(
          0x3B2112, 0x75421F, 0xB77B3E, 0x3F2112,
          0xD7B47A, 0xF1D79E, 0x9B6C39, 0xA67845,
          7, 0.17F, 0x593014, 0x51402D, 0x2D7B35, 0x85520A);
      case HEAVY_FRAME -> new BodyPalette(
          0x25140B, 0x653619, 0xA26732, 0x2A160B,
          0xCDAF79, 0xE9D19A, 0x7D552F, 0x7A5636,
          6, 0.21F, 0x472611, 0x443525, 0x286C31, 0x744506);
      case CLEAN_CLOTH -> new BodyPalette(
          0x332012, 0x6F4322, 0xB0773B, 0x3A2212,
          0xDCC38D, 0xF3E0AA, 0x9F7445, 0xA98255,
          9, 0.12F, 0x543018, 0x4C4030, 0x2F7B37, 0x875306);
    };
  }

  private static int withAlpha(int rgb, float alpha) {
    int clamped = Math.max(0, Math.min(255, Math.round(alpha * 255F)));
    return (clamped << 24) | (rgb & 0xFFFFFF);
  }

  private static final class BodyPalette {
    private final int outerWood;
    private final int innerWood;
    private final int woodHighlight;
    private final int woodShadow;
    private final int cloth;
    private final int clothLight;
    private final int clothShadow;
    private final int thread;
    private final int threadSpacing;
    private final float threadAlpha;
    private final int taskTitleText;
    private final int requirementText;
    private final int completeText;
    private final int rewardText;

    private BodyPalette(
        int outerWood,
        int innerWood,
        int woodHighlight,
        int woodShadow,
        int cloth,
        int clothLight,
        int clothShadow,
        int thread,
        int threadSpacing,
        float threadAlpha,
        int taskTitleText,
        int requirementText,
        int completeText,
        int rewardText) {
      this.outerWood = outerWood;
      this.innerWood = innerWood;
      this.woodHighlight = woodHighlight;
      this.woodShadow = woodShadow;
      this.cloth = cloth;
      this.clothLight = clothLight;
      this.clothShadow = clothShadow;
      this.thread = thread;
      this.threadSpacing = threadSpacing;
      this.threadAlpha = threadAlpha;
      this.taskTitleText = taskTitleText;
      this.requirementText = requirementText;
      this.completeText = completeText;
      this.rewardText = rewardText;
    }
  }

  private record RenderTask(TaskSnapshot task, float alpha) {
  }

  private static final class TaskRenderState {
    private TaskSnapshot task;
    private long completeSince = -1L;
    private long missingSince = -1L;

    private TaskRenderState(TaskSnapshot task, long now) {
      this.task = task;
      update(task, now);
    }

    private void update(TaskSnapshot task, long now) {
      this.task = task;
      this.missingSince = -1L;
      if (task.complete()) {
        if (this.completeSince < 0L) {
          this.completeSince = now;
        }
      } else {
        this.completeSince = -1L;
      }
    }

    private void markMissing(long now) {
      if (this.missingSince < 0L) {
        this.missingSince = now;
      }
    }

    private float alpha(long now) {
      long fadeStart = fadeStart();
      if (fadeStart < 0L || now < fadeStart) {
        return 1F;
      }
      return 1F - Math.min(1F, (now - fadeStart) / (float) FADE_MS);
    }

    private long fadeStart() {
      if (this.completeSince < 0L) {
        return this.missingSince >= 0L ? this.missingSince : -1L;
      }
      return this.completeSince + (this.task.requiresTurnIn() ? 0L : PURE_COMPLETE_HOLD_MS);
    }
  }
}
