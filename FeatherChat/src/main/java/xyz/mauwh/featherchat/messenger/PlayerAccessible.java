package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Collection;
import java.util.UUID;

public interface PlayerAccessible extends Player {
    void setDisplayName(@NotNull Component displayName, boolean update);
    void setChannels(@NotNull Collection<UUID> channels);
}
