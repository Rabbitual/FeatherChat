package xyz.mauwh.featherchat.api.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Objects;

public final class ChannelInvitation {

    private final UserChatChannel channel;
    private final Player inviter;
    private final Player invitee;
    private final long expiryTime;

    public ChannelInvitation(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee, long expiryTimeMillis) {
        this.channel = channel;
        this.inviter = inviter;
        this.invitee = invitee;
        this.expiryTime = System.currentTimeMillis() + expiryTimeMillis;
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

    public long getExpiryTime() {
        return expiryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final ChannelInvitation other = (ChannelInvitation) o;
        return channel.equals(other.channel) && inviter.equals(other.inviter)
                && invitee.equals(other.invitee) && expiryTime == other.expiryTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, inviter, invitee, expiryTime);
    }

}
