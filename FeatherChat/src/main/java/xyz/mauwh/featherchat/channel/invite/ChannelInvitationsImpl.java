package xyz.mauwh.featherchat.channel.invite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitation;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.scheduler.FeatherChatScheduler;
import xyz.mauwh.featherchat.scheduler.FeatherChatTask;

import java.util.*;

public class ChannelInvitationsImpl implements ChannelInvitations {

    private final FeatherChatScheduler scheduler;
    private final Set<ChannelInvitation> invites;
    private FeatherChatTask task;

    public ChannelInvitationsImpl(@NotNull FeatherChatScheduler scheduler) {
        this.scheduler = scheduler;
        this.invites = new HashSet<>();
    }

    @Override
    @NotNull
    public ChannelInvitation invite(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee) {
        ChannelInvitation invite = new ChannelInvitation(channel, inviter, invitee);
        invites.add(invite);
        if (task == null || task.isCancelled()) {
            task = scheduler.executeTaskRepeating(() -> {
                if (invites.remove(invite)) {
                    invitee.sendMessage(Component.text("Your channel invite has expired", NamedTextColor.RED));
                }
            }, 20L, 20L);
        }
        return invite;
    }

    @Override
    public boolean isInvited(@NotNull Player invitee, @NotNull UserChatChannel channel) {
        for (ChannelInvitation invite : invites) {
            if (invite.getChannel().equals(channel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptInvitation(@NotNull ChannelInvitation invite) {
        if (!invites.remove(invite)) {
            return false;
        }
        Player invitee = invite.getInvitee();
        UserChatChannel channel = invite.getChannel();
        invitee.addChannel(channel);
        channel.addMember(invitee);
        return true;
    }

    @Override
    public boolean denyInvitation(@NotNull ChannelInvitation invite) {
        return invites.remove(invite);
    }

}
