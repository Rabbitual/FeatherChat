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
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

@CommandAlias("featherchat|fc")
@Subcommand("channel|ch")
@CommandPermission("featherchat.channel")
public final class FeatherChatChannelSubcommand extends BaseCommand {

    private final FeatherChatPlugin plugin;
    private final ChatChannels channelRepository;

    public FeatherChatChannelSubcommand(@NotNull FeatherChatPlugin plugin) {
        this.plugin = plugin;
        this.channelRepository = plugin.getChannels();
    }

    @Subcommand("create")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.channel.create")
    public void onCreate(@NotNull Player issuer, @NotNull String channelName) {
        try {
            ChatChannel channel = channelRepository.createChannel(issuer, channelName);
            issuer.sendMessage(Component.text("Successfully created channel '" + channelName + " (" + channel.getKey() + ")'", NamedTextColor.GREEN));
        } catch (IllegalArgumentException err) {
            issuer.sendMessage(Component.text(err.getMessage(), NamedTextColor.RED));
        }
    }

    @Subcommand("invite")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.channel.invite")
    public void onInvite(@NotNull Player issuer, @NotNull @Conditions("isOwner") UserChatChannel channel, @NotNull Player invitee) {
        plugin.getInvitations().invite(channel, issuer, invitee);
        Component channelName = channel.getFriendlyName();
        Component inviteeName = invitee.getFriendlyName();
        Component message = inviteeName.append(Component.text(" has been invited to ", NamedTextColor.GREEN)).append(channelName);
        issuer.sendMessage(message);
    }

    @Subcommand("chat")
    @CommandCompletion("@channels")
    @CommandPermission("featherchat.channel.chat")
    public void onChat(@NotNull Player issuer, @NotNull @Conditions("isMember") UserChatChannel channel, @NotNull @Single String message) {
        channel.sendMessage(issuer, Component.text(message));
    }

    @Subcommand("displayname")
    @Conditions("playerOnly")
    @CommandAlias("nickname|nick")
    @CommandPermission("featherchat.channel.displayname")
    public void onDisplayName(@NotNull Player issuer, @NotNull @Conditions("isOwner") UserChatChannel channel, @NotNull @Single @Conditions("channelName|charLimit:max=56") String displayName) {
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        channel.setDisplayName(serialized);
        issuer.sendMessage(Component.text("Changed display name of channel to '", NamedTextColor.RED)
                .append(serialized).append(Component.text("'", NamedTextColor.RED)));
    }

}
