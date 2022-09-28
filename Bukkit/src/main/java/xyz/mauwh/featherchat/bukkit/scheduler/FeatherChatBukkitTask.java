package xyz.mauwh.featherchat.bukkit.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.scheduler.FeatherChatTask;

public class FeatherChatBukkitTask implements FeatherChatTask {

    private final BukkitTask task;

    public FeatherChatBukkitTask(@NotNull BukkitTask task) {
        this.task = task;
    }

    public int getTaskId() {
        return task.getTaskId();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public void cancel() {
        task.cancel();
    }

}
