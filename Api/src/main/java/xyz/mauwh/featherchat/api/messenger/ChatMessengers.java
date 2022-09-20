package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ChatMessengers<T, U extends ChatMessenger<T>, V extends Player<T>> {
    @NotNull U getConsole();
    @NotNull U getBySender(@NotNull T sender);
    @NotNull V getByUUID(@NotNull UUID uuid);
    void update(@NotNull V player);
    void updateAndRemove(@NotNull V player);
}
