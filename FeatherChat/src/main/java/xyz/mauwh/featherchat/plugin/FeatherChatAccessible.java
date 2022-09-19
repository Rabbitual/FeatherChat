package xyz.mauwh.featherchat.plugin;

import net.kyori.adventure.platform.AudienceProvider;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.FeatherChat;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;

public interface FeatherChatAccessible extends FeatherChat, PluginAccessible {
    @NotNull AudienceProvider getAdventure();
    @NotNull ChannelMessageHandler getMessageHandler();
}
