package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ChatMessengers<T, U extends ChatMessenger, V extends Player> {
    @NotNull U getConsole();
    @NotNull U getBySender(@NotNull T sender);
    @NotNull V getByUUID(@NotNull UUID uuid);
    void update(@NotNull V player);
    void updateAndRemove(@NotNull V player);
}
