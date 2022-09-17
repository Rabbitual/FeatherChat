package xyz.mauwh.featherchat.channel;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UserChatChannel extends ChatChannel {
    @NotNull UUID getOwner();
    void setOwner(@NotNull UUID owner);
}
