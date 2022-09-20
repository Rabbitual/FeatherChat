package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class ChatMessengerFactory<T, U extends ChatMessenger<T>, V extends Player<T>> {

    @NotNull
    public abstract U console();

    @NotNull
    public abstract U sender(@NotNull T sender);

    @NotNull
    public abstract V player(@NotNull UUID player);

    @NotNull
    public abstract V offlinePlayer(@NotNull UUID player, String name);

}
