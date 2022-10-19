package xyz.mauwh.featherchat.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class UUIDPair extends Pair<UUID, UUID> {

    public UUIDPair(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        super(channelUUID, playerUUID);
    }

    @NotNull
    public UUID getChannelUUID() {
        return getLeft();
    }

    @NotNull
    public UUID getPlayerUUID() {
        return getRight();
    }

}
