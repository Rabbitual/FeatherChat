package xyz.mauwh.featherchat.plugin;

import co.aikar.commands.CommandExecutionContext;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.commands.ConditionContext;
import net.kyori.adventure.platform.AudienceProvider;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.FeatherChat;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;

import java.io.File;
import java.util.logging.Logger;

public interface FeatherChatPlugin extends FeatherChat {
    void enable();
    void disable();
    @NotNull AudienceProvider getAdventure();
    @NotNull ChannelMessageHandler getMessageHandler();
    @NotNull File getDataFolder();
    @NotNull Logger getLogger();

    <IT, I extends CommandIssuer,
            CEC extends CommandExecutionContext<CEC, I>,
            CC extends ConditionContext<I>> void setupCommandManager(@NotNull CommandManager<IT, I, ?, ?, CEC, CC> commandManager);
}
