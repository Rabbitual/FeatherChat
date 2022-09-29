package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

@CommandAlias("featherchat|fc")
@Subcommand("debug")
@CommandPermission("featherchat.debug")
public final class FeatherChatDebugSubcommand extends BaseCommand {

    private final ChatChannel debugChannel;

    public FeatherChatDebugSubcommand(@NotNull ChatChannels channels) {
        this.debugChannel = channels.getDebugChannel();
    }

    @Subcommand("chat")
    public void onDebugChat(@NotNull ChatMessenger issuer, @NotNull String message) {
        debugChannel.sendMessage(issuer, Component.text(message));
    }

    @Subcommand("format")
    public void onDebugFormat(@NotNull String format) {
        debugChannel.setMessageFormat(format);
    }

    @Subcommand("displayname")
    public void onDebugName(@Single @NotNull String name) {
        debugChannel.setDisplayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

}