package xyz.mauwh.featherchat.api.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Set;

public interface ChannelInvitations {
    /**
     * Creates a timed pending channel invitation.
     * @param channel - The channel to be invited to
     * @param inviter - The player sending an invite
     * @param invitee - The player being invited
     * @return The pending invitation
     */
    @NotNull ChannelInvitation invite(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee);

    /**
     * Gets all pending channel invitations for the provided player
     * @param player - The player
     * @return A set of all pending channel invitations for the player
     */
    @NotNull Set<ChannelInvitation> getInvitations(Player player);

    /**
     * Checks if the provided player has been invited to a channel
     * @param invitee - The player who may have been invited
     * @param channel - The target channel
     * @return true if the player has been invited to the channel
     */
    boolean isInvited(@NotNull Player invitee, @NotNull UserChatChannel channel);

    /**
     * Removes a pending invitation.
     * @param invite - The invitation to be removed
     * @return true if an invitation was successfully removed
     */
    boolean removeInvitation(@NotNull ChannelInvitation invite);

    /**
     * Removes a pending invitation by its invitee and target channel.
     * @param invitee - The invitee
     * @param channel - The target channel
     * @return true if an invitation was successfully removed
     */
    boolean removeInvitation(@NotNull Player invitee, @NotNull UserChatChannel channel);
}
