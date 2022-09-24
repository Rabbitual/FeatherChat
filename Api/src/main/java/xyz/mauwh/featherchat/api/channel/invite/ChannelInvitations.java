package xyz.mauwh.featherchat.api.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;

public interface ChannelInvitations {

    @NotNull ChannelInvitation invite(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee);

    boolean isInvited(@NotNull Player invitee, @NotNull UserChatChannel channel);

    boolean acceptInvitation(@NotNull ChannelInvitation invite);

    boolean denyInvitation(@NotNull ChannelInvitation invite);
}
