package com.xiwanzi.otherworldinnhud.client.overlays;

import com.xiwanzi.otherworldinnhud.Common;
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

public final class TaskHudOverlayCommon {
  private static final ResourceLocation STORY_TITLE_TEXTURE =
      Common.location("textures/gui/task_hud/story_wish_title.png");
  private static final ResourceLocation TOWN_TITLE_TEXTURE =
      Common.location("textures/gui/task_hud/town_commission_title.png");
  private static final int BLOCK_WIDTH = 176;
  private static final int TEXT_WIDTH = 172;
  private static final int TITLE_WIDTH = 92;
  private static final int TITLE_HEIGHT = 18;
  private static final int BODY_LEFT_PADDING = 0;
  private static final int BODY_TOP_PADDING = 2;
  private static final int TITLE_TO_BODY_GAP = 3;
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
    int screenWidth = mc.getWindow().getGuiScaledWidth();
    int rightMargin = OtherworldInnHudClient.getTaskHudX();
    int topMargin = getTopMargin(mc);
    int x = (int) ((screenWidth - rightMargin - Math.ceil(BLOCK_WIDTH * scale)) / scale);
    int y = (int) (topMargin / scale);
    int gap = (mc.font.lineHeight * OtherworldInnHudClient.getTaskHudGapLines()) + 4;

    graphics.pose().pushPose();
    graphics.pose().scale((float) scale, (float) scale, 1F);
    int blockY = y;
    for (RenderTask renderTask : renderTasks) {
      int blockHeight = renderTask(graphics, mc, renderTask, x, blockY);
      blockY += blockHeight + gap;
    }
    graphics.pose().popPose();
  }

  private static boolean shouldDraw(Minecraft mc) {
    return OtherworldInnHudClient.getEnableMod() && OtherworldInnHudClient.getEnableTaskHud()
        && Common.vanillaShouldDrawHud(mc);
  }

  private static int getTopMargin(Minecraft mc) {
    int topMargin = OtherworldInnHudClient.getTaskHudY();
    if (CurrentMinimap.hasVisibleMinimap(mc)) {
      topMargin = Math.max(topMargin, OtherworldInnHudClient.getTaskHudMinimapReservedHeight());
    }
    return topMargin;
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

  private static int renderTask(GuiGraphics graphics, Minecraft mc, RenderTask renderTask, int x, int y) {
    TaskSnapshot task = renderTask.task;
    float alpha = renderTask.alpha;
    Font font = mc.font;
    int titleX = x + BODY_LEFT_PADDING;
    renderTitle(graphics, mc, task, titleX, y, alpha);

    int textX = x + BODY_LEFT_PADDING;
    int lineY = y + TITLE_HEIGHT + TITLE_TO_BODY_GAP;
    lineY = drawWrapped(graphics, font, task.title(), textX, lineY, TASK_TITLE_COLOR, alpha, true);
    lineY += BODY_TOP_PADDING;

    for (RequirementSnapshot requirement : task.requirements()) {
      MutableComponent line = Common.literalText(requirement.complete() ? "[x] " : "[ ] ");
      line.append(requirement.display());
      if (requirement.required() > 1 || requirement.current() > 0) {
        line.append(Common.literalText(" " + requirement.current() + "/" + requirement.required()));
      }
      int color = requirement.complete() ? REQUIREMENT_COMPLETE_COLOR : REQUIREMENT_OPEN_COLOR;
      lineY = drawWrapped(graphics, font, line, textX, lineY, color, alpha, true);
    }

    if (!task.rewards().isEmpty()) {
      lineY += BODY_TOP_PADDING;
      lineY = drawWrapped(graphics, font, formatRewards(task.rewards()), textX, lineY, REWARD_COLOR, alpha, true);
    }

    return Math.max(TITLE_HEIGHT, lineY - y);
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

  private static int withAlpha(int rgb, float alpha) {
    int clamped = Math.max(0, Math.min(255, Math.round(alpha * 255F)));
    return (clamped << 24) | (rgb & 0xFFFFFF);
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
