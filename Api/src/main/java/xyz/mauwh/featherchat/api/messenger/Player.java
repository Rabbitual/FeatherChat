package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;

import java.util.Set;
import java.util.UUID;

public interface Player extends ChatMessenger {
    /**
     * @return The UUID of this player
     */
    @NotNull UUID getUUID();

    /**
     * @return The name of this player
     */
    @NotNull String getName();

    /**
     * Gets a set of UUIDs of channels this player belongs to
     * @return A set of UUIDs of channels this player belongs to
     */
    @NotNull Set<UUID> getChannels();

    /**
     * Adds the provided channel to the channels that this player belongs to. This must be invoked in tandem with
     * {@link xyz.mauwh.featherchat.api.channel.UserChatChannel#addMember(Player)}.
     * @param channel - The channel to be added to this player
     * @return true if the channel was successfully added, else false
     */
    boolean addChannel(@NotNull ChatChannel channel);

    /**
     * Removes the provided channel from the channels that this player belongs to. This must be invoked in tandem with
     * {@link xyz.mauwh.featherchat.api.channel.UserChatChannel#removeMember(Player)}.
     * @param channel - The channel to be removed from this player
     * @return true if the channel was successfully removed, else false
     */
    boolean removeChannel(@NotNull ChatChannel channel);

    /**
     * Validates this player's channels by loading them and removing any which are invalid or do not consider this
     * player a member.
     */
    void validateChannels();

    /**
     * Checks if this player is currently online
     * @return true if this player is online
     */
    boolean isOnline();
}
