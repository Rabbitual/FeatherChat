package xyz.mauwh.featherchat.scheduler;

import org.jetbrains.annotations.NotNull;

public interface FeatherChatScheduler {
    @NotNull FeatherChatTask executeTaskLater(@NotNull Runnable task, long delayInTicks);
    @NotNull FeatherChatTask executeTaskRepeating(@NotNull Runnable task, long initialDelayInTicks, long intervalInTicks);
}
