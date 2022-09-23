package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ChatMessengers<T, U extends ChatMessenger, V extends Player> {
    @NotNull U getConsole();
    @NotNull U getBySender(@NotNull T sender);
    @Nullable V getByName(@NotNull String name);
    @NotNull V getByUUID(@NotNull UUID uuid);
    void update(@NotNull V player);
    void updateAndRemove(@NotNull V player);
}
