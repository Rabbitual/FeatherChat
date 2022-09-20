package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

@CommandAlias("featherchat|fc")
@Subcommand("channel|ch")
@CommandPermission("featherchat.channel")
public final class FeatherChatChannelSubcommand extends BaseCommand {

    private final FeatherChatAccessible plugin;
    private final ChatChannels channelRepository;

    public FeatherChatChannelSubcommand(@NotNull FeatherChatAccessible plugin) {
        this.plugin = plugin;
        this.channelRepository = plugin.getChannels();
    }

    @Subcommand("create")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.channel.create")
    public void onCreate(@NotNull CommandIssuer issuer, @NotNull String channelName) {
        Player<?> player = plugin.getMessengers().getByUUID(issuer.getUniqueId());
        try {
            ChatChannel channel = channelRepository.createChannel(player, channelName);
            player.sendMessage(Component.text("Successfully created channel '" + channelName + " (" + channel.getKey() + ")'", NamedTextColor.GREEN));
        } catch (IllegalArgumentException err) {
            player.sendMessage(Component.text(err.getMessage(), NamedTextColor.RED));
        }
    }

    @Subcommand("chat")
    @CommandCompletion("@channels")
    @CommandPermission("featherchat.channel.chat")
    public void onChat(@NotNull CommandIssuer issuer, @NotNull @Conditions("isMember") UserChatChannel channel, @NotNull @Single String message) {
        ChatMessenger<?> messenger = plugin.getMessengers().getByUUID(issuer.getUniqueId());
        channel.sendMessage(messenger, Component.text(message));
    }

    @Subcommand("displayname")
    @Conditions("playerOnly")
    @CommandAlias("nickname|nick")
    @CommandPermission("featherchat.channel.displayname")
    public void onDisplayName(@NotNull CommandIssuer issuer, @NotNull @Conditions("isOwner") UserChatChannel channel, @NotNull @Single @Conditions("channelName|charLimit:max=56") String displayName) {
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        channel.setDisplayName(serialized);
        plugin.getMessengers().getByUUID(issuer.getUniqueId()).sendMessage(
                Component.text("Changed display name of channel to '", NamedTextColor.RED).append(serialized).append(Component.text("'", NamedTextColor.RED))
        );
    }

}
