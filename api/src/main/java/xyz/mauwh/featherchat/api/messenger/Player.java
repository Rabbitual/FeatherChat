package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;

import java.util.Set;
import java.util.UUID;

public interface Player<T> extends ChatMessenger<T> {
    @NotNull UUID getUUID();
    @NotNull String getName();
    @NotNull Set<UUID> getChannels();

    void addChannel(@NotNull ChatChannel channel);

    void removeChannel(@NotNull ChatChannel channel);

    void validateChannels();
    boolean isOnline();
}
