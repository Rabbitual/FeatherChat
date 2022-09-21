package xyz.mauwh.featherchat.api.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

public final class ChannelInvitation {

    private final UserChatChannel channel;
    private final ChatMessenger inviter;
    private final ChatMessenger invitee;

    public ChannelInvitation(@NotNull UserChatChannel channel, @NotNull ChatMessenger inviter, @NotNull ChatMessenger invitee) {
        this.channel = channel;
        this.inviter = inviter;
        this.invitee = invitee;
    }

    @NotNull
    public UserChatChannel getChannel() {
        return channel;
    }

    @NotNull
    public ChatMessenger getInviter() {
        return inviter;
    }

    @NotNull
    public ChatMessenger getInvitee() {
        return invitee;
    }

}
