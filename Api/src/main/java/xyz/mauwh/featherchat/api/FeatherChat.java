package xyz.mauwh.featherchat.api;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;

public interface FeatherChat {
    @NotNull ChatMessengers<?, ?, ?> getMessengers();
    @NotNull ChatChannels getChannels();
    @NotNull ChannelInvitations getInvitations();
    @NotNull String getVersion();
}
