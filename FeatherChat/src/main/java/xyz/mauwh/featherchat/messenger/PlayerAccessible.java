package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Set;
import java.util.UUID;

public interface PlayerAccessible<T> extends Player<T> {
    void setDisplayName(@NotNull Component displayName, boolean update);
    void setChannels(@NotNull Set<UUID> channels);
}
