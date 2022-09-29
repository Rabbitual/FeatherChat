package xyz.mauwh.featherchat.api.channel;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;

public interface UserChatChannel extends ChatChannel {
    /**
     * Gets the UUID of this channel's owner.
     * @return The channel owner's UUID
     */
    @NotNull UUID getOwner();

    /**
     * Sets the owner of this channel.
     * @param owner - The new channel owner
     */
    void setOwner(@NotNull UUID owner);

    /**
     * Sets the owner of this channel.
     * @param owner - The new channel owner
     */
    void setOwner(@NotNull Player owner);

    /**
     * Removes the provided player from this channel's members.
     * @param player - The player to be removed as a member
     * @return true if this player was successfully removed as a member
     */
    boolean removeMember(@NotNull Player player);
}
