package xyz.mauwh.featherchat.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.scheduler.FeatherChatScheduler;

public final class FeatherChatBukkitScheduler implements FeatherChatScheduler<FeatherChatBukkitTask> {

    private final FeatherChatBukkit plugin;

    public FeatherChatBukkitScheduler(@NotNull FeatherChatBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public FeatherChatBukkitTask executeTaskLater(@NotNull Runnable task, long delayInTicks) {
        return new FeatherChatBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayInTicks));
    }

    @Override
    @NotNull
    public FeatherChatBukkitTask executeTaskRepeating(@NotNull Runnable task, long initialDelayInTicks, long intervalInTicks) {
        return new FeatherChatBukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayInTicks, intervalInTicks));
    }

    @Override
    public void cancelTask(@NotNull FeatherChatBukkitTask task) {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

}
