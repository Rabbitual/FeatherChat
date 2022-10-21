package xyz.mauwh.featherchat.api.channel.member;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class ChannelMember {

    private final UUID channelUUID;
    private final UUID playerUUID;
    private String name;
    private Component displayName;

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

    public void setName(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    public Component getDisplayName() {
        return displayName == null ? Component.text(name) : displayName;
    }

}
