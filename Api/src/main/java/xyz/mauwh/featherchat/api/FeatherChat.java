package xyz.mauwh.featherchat.api;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;

public interface FeatherChat {
    /**
     * @return the chat messenger handler of this FeatherChat instance
     */
    @NotNull ChatMessengers<?> getMessengers();

    /**
     * @return the chat channel repository of this FeatherChat instance
     */
    @NotNull ChatChannels getChannels();

    /**
     * @return the channel invitation handler of this FeatherChat instance
     */
    @NotNull ChannelInvitations getInvitations();

    /**
     * @return the version of FeatherChat currently running
     */
    @NotNull String getVersion();
}
