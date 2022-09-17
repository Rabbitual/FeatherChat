package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.channel.ChatChannel;
import xyz.mauwh.featherchat.channel.UserChatChannel;
import xyz.mauwh.featherchat.messenger.ChatMessenger;
import xyz.mauwh.featherchat.channel.ChatChannelRepository;

@CommandAlias("featherchat|fc")
@Subcommand("channel|ch")
@CommandPermission("featherchat.channel")
public final class FeatherChatChannelSubcommand extends BaseCommand {

    private final ChatChannelRepository channelRepository;

    public FeatherChatChannelSubcommand(@NotNull ChatChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Subcommand("create")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.channel.create")
    public void onCreate(@NotNull CommandIssuer issuer, @NotNull String channelName) {
        xyz.mauwh.featherchat.messenger.Player player = ChatMessenger.player((Player)issuer.getIssuer());
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
        ChatMessenger messenger = ChatMessenger.sender(issuer.getIssuer());
        channel.sendMessage(messenger, Component.text(message));
    }

    @Subcommand("displayname")
    @Conditions("playerOnly")
    @CommandAlias("nickname|nick")
    @CommandPermission("featherchat.channel.displayname")
    public void onDisplayName(@NotNull CommandIssuer issuer, @NotNull @Conditions("isOwner") UserChatChannel channel, @NotNull @Single @Conditions("channelName|charLimit:max=56") String displayName) {
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        channel.setDisplayName(serialized);
        ChatMessenger.player((Player)issuer.getIssuer()).sendMessage(
                Component.text("Changed display name of channel to '", NamedTextColor.RED).append(serialized).append(Component.text("'", NamedTextColor.RED))
        );
    }

}
