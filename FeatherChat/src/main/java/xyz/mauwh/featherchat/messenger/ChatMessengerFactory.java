package xyz.mauwh.featherchat.messenger;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;

public abstract class ChatMessengerFactory<T, U extends ChatMessenger, V extends Player> {

    @NotNull
    public abstract U console();

    @NotNull
    public abstract U sender(@NotNull T sender);

    @NotNull
    public abstract V player(@NotNull UUID player);

    @NotNull
    public abstract V offlinePlayer(@NotNull UUID player, String name);

}
