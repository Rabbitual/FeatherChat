package xyz.mauwh.featherchat.api.channel;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;

public interface UserChatChannel extends ChatChannel {
    @NotNull UUID getOwner();
    void setOwner(@NotNull UUID owner);

    boolean removeMember(@NotNull Player player);
}
