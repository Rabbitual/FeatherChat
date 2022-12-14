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

public class ChannelInvitationsImpl<T extends FeatherChatScheduler<U>, U extends FeatherChatTask> implements ChannelInvitations {

    private final T scheduler;
    private final Map<Player, Set<ChannelInvitation>> invites;
    private U task;

    public ChannelInvitationsImpl(@NotNull T scheduler) {
        this.scheduler = scheduler;
        this.invites = new HashMap<>();
    }

    @Override
    @NotNull
    public ChannelInvitation invite(@NotNull UserChatChannel channel, @NotNull Player inviter, @NotNull Player invitee) {
        ChannelInvitation invite = new ChannelInvitation(channel, inviter, invitee, 60000L);
        Set<ChannelInvitation> playerInvites = invites.computeIfAbsent(invitee, k -> new HashSet<>());
        playerInvites.add(invite);
        if (task == null || task.isCancelled()) {
            scheduleInviteExpiration();
        }
        return invite;
    }

    @Override
    @NotNull
    public Set<ChannelInvitation> getInvitations(Player player) {
        Set<ChannelInvitation> playerInvites = invites.get(player);
        return playerInvites != null ? Set.copyOf(playerInvites) : Collections.emptySet();
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
    public boolean removeInvitation(@NotNull ChannelInvitation invite) {
        Set<ChannelInvitation> playerInvites = invites.get(invite.getInvitee());
        if (playerInvites != null && playerInvites.remove(invite)) {
            if (playerInvites.isEmpty()) {
                invites.remove(invite.getInvitee());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeInvitation(@NotNull Player invitee, @NotNull UserChatChannel channel) {
        Set<ChannelInvitation> playerInvites = invites.get(invitee);
        if (playerInvites == null) {
            return false;
        }
        Iterator<ChannelInvitation> iter = playerInvites.iterator();
        while (iter.hasNext()) {
            UserChatChannel channel1 = iter.next().getChannel();
            if (!channel.getUUID().equals(channel1.getUUID())) {
                continue;
            }
            iter.remove();
            if (playerInvites.isEmpty()) {
                invites.remove(invitee);
            }
            return true;
        }
        return false;
    }

    private void scheduleInviteExpiration() {
        task = scheduler.executeTaskRepeating(() -> {
            Iterator<Map.Entry<Player, Set<ChannelInvitation>>> mapIter = invites.entrySet().iterator();
            while (mapIter.hasNext()) {
                Map.Entry<Player, Set<ChannelInvitation>> entry = mapIter.next();
                Player invitee = entry.getKey();
                Set<ChannelInvitation> playerInvites = entry.getValue();
                playerInvites.removeIf(invitation -> {
                    boolean expired = invitation.getExpiryTime() < System.currentTimeMillis();
                    if (expired && invitee.isOnline()) {
                        invitee.sendMessage(Component.text("Your channel invite has expired", NamedTextColor.RED));
                    }
                    return expired;
                });
                if (playerInvites.isEmpty()) {
                    mapIter.remove();
                }
                scheduler.cancelTask(task);
            }
        }, 20L, 20L);
    }

}
