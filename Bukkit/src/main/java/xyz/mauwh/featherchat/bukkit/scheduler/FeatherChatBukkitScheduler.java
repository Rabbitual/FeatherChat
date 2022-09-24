package xyz.mauwh.featherchat.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.scheduler.FeatherChatScheduler;
import xyz.mauwh.featherchat.scheduler.FeatherChatTask;

public final class FeatherChatBukkitScheduler implements FeatherChatScheduler {

    private final FeatherChatBukkit plugin;

    public FeatherChatBukkitScheduler(@NotNull FeatherChatBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public FeatherChatTask executeTaskLater(@NotNull Runnable task, long delayInTicks) {
        return new FeatherChatBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayInTicks));
    }

    @Override
    @NotNull
    public FeatherChatTask executeTaskRepeating(@NotNull Runnable task, long initialDelayInTicks, long intervalInTicks) {
        return new FeatherChatBukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayInTicks, intervalInTicks));
    }

}
