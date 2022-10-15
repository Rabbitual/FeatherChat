package xyz.mauwh.featherchat.api.channel.member;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ChannelMember {

    private final UUID channelUUID;
    private final UUID playerUUID;

    public ChannelMember(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        this.channelUUID = channelUUID;
        this.playerUUID = playerUUID;
    }

    @NotNull
    public UUID getChannelUUID() {
        return channelUUID;
    }

    @NotNull
    public UUID getPlayerUUID() {
        return playerUUID;
    }

}
