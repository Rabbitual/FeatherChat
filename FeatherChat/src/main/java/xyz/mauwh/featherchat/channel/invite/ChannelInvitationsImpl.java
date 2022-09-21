package xyz.mauwh.featherchat.channel.invite;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitation;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.*;

public class ChannelInvitationsImpl implements ChannelInvitations {

    private final Map<Player, Set<ChannelInvitation>> invites;

    public ChannelInvitationsImpl() {
        this.invites = new HashMap<>();
    }

    @Override
    @NotNull
    public ChannelInvitation invite(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee) {
        Set<ChannelInvitation> playerInvites = invites.computeIfAbsent(invitee, invitee1 -> new HashSet<>());
        ChannelInvitation invite = new ChannelInvitation(channel, inviter, invitee);
        playerInvites.add(invite);
        return invite;
    }

    @Override
    public boolean isInvited(@NotNull Player invitee, @NotNull UserChatChannel channel) {
        Set<ChannelInvitation> playerInvites = invites.get(invitee);
        if (playerInvites == null) {
            return false;
        }
        for (ChannelInvitation invite : playerInvites) {
            if (invite.getChannel().equals(channel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptInvitation(@NotNull Player invitee, @NotNull UserChatChannel channel) {
        Set<ChannelInvitation> playerInvites = invites.get(invitee);
        if (playerInvites == null) {
            return false;
        }
        Iterator<ChannelInvitation> iter = playerInvites.iterator();
        while (iter.hasNext()) {
            if (iter.next().getChannel().equals(channel)) {
                iter.remove();
                invitee.addChannel(channel);
                channel.addMember(invitee);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean denyInvitation(@NotNull Player invitee, @NotNull UserChatChannel channel) {
        Set<ChannelInvitation> playerInvites = invites.get(invitee);
        if (playerInvites == null) {
            return false;
        }
        Iterator<ChannelInvitation> iter = playerInvites.iterator();
        while (iter.hasNext()) {
            if (iter.next().getChannel().equals(channel)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

}
