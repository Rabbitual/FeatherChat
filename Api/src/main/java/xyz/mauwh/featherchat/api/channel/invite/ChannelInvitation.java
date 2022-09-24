package xyz.mauwh.featherchat.api.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;

public final class ChannelInvitation {

    private final UserChatChannel channel;
    private final Player inviter;
    private final Player invitee;
    private final long timestamp;

    public ChannelInvitation(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee) {
        this.channel = channel;
        this.inviter = inviter;
        this.invitee = invitee;
        this.timestamp = System.currentTimeMillis();
    }

    @NotNull
    public UserChatChannel getChannel() {
        return channel;
    }

    @NotNull
    public Player getInviter() {
        return inviter;
    }

    @NotNull
    public Player getInvitee() {
        return invitee;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
