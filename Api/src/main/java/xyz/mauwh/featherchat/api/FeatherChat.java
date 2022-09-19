package xyz.mauwh.featherchat.api;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.messenger.ChatMessengerFactory;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;

public interface FeatherChat {
    @NotNull ChatMessengerFactory<?, ?, ?> getMessengerFactory();
    @NotNull ChatMessengers<?, ?, ?> getMessengers();
    @NotNull ChatChannels getChannels();
    @NotNull String getVersion();
}
