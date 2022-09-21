package xyz.mauwh.featherchat.plugin;

import net.kyori.adventure.platform.AudienceProvider;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.FeatherChat;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;

import java.io.File;
import java.util.logging.Logger;

public interface FeatherChatPlugin extends FeatherChat {
    @NotNull AudienceProvider getAdventure();
    @NotNull ChannelMessageHandler getMessageHandler();
    @NotNull File getDataFolder();
    @NotNull Logger getLogger();
}
