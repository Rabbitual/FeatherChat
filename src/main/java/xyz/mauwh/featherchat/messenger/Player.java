package xyz.mauwh.featherchat.messenger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Player extends ChatMessenger {
    @NotNull UUID getUUID();
    @NotNull String getName();
    @Nullable org.bukkit.entity.Player getHandle();
    @NotNull Set<UUID> getChannels();
    void validateChannels();
    boolean isOnline();
}
