package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

@CommandAlias("featherchat|fc")
@Subcommand("debug")
@CommandPermission("featherchat.debug")
public final class FeatherChatDebugSubcommand extends BaseCommand {

    private final ChatMessengers<?, ?, ?> messengers;
    private final ChatChannel debugChannel;

    public FeatherChatDebugSubcommand(@NotNull FeatherChatPlugin plugin) {
        this.messengers = plugin.getMessengers();
        this.debugChannel = plugin.getChannels().getDebugChannel();
    }

    @Subcommand("chat")
    public void onDebugChat(@NotNull CommandIssuer sender, @NotNull String message) {
        ChatMessenger messenger = sender.isPlayer() ? messengers.getByUUID(sender.getUniqueId()) : messengers.getConsole();
        debugChannel.sendMessage(messenger, Component.text(message));
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