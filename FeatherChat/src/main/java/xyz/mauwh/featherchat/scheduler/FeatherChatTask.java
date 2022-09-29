package xyz.mauwh.featherchat.scheduler;

public interface FeatherChatTask {
    boolean isCancelled();
    void cancel();
}
