package xyz.mauwh.featherchat.scheduler;

import org.jetbrains.annotations.NotNull;

public interface FeatherChatScheduler<U extends FeatherChatTask> {
    @NotNull U executeTaskLater(@NotNull Runnable task, long delayInTicks);
    @NotNull U executeTaskRepeating(@NotNull Runnable task, long initialDelayInTicks, long intervalInTicks);
    void cancelTask(@NotNull U task);
}
