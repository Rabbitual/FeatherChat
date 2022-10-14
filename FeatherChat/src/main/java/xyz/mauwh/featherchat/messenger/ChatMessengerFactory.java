package xyz.mauwh.featherchat.messenger;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;

public abstract class ChatMessengerFactory<T> {

    @NotNull
    public abstract ChatMessenger console();

    @NotNull
    public abstract ChatMessenger sender(@NotNull T sender);

    @NotNull
    public abstract Player player(@NotNull UUID player) throws IllegalArgumentException;

    @NotNull
    public abstract Player player(@NotNull UUID player, @NotNull String name);

}
