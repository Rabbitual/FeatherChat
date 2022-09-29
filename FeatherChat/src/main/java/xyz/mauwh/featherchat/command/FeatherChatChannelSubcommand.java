package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
            issuer.sendMessage(text("Successfully created channel '" + channelName + " (" + channel.getKey() + ")'", GREEN));
        } catch (IllegalArgumentException err) {
            throw new InvalidCommandArgument(err.getMessage(), false);
        }
    }

    @Subcommand("invite|inv")
    @Conditions("playerOnly")
    @CommandCompletion("@channels:owner @players")
    @CommandPermission("featherchat.channel.invite")
    public void onInvite(@NotNull Player issuer, @NotNull @Flags("owned") UserChatChannel channel, @NotNull @Flags("other") Player invitee) {
        if (channel.isMember(invitee)) {
            throw new InvalidCommandArgument("Invitee already belongs to the specified channel", false);
        }
        plugin.getInvitations().invite(channel, issuer, invitee);
        Component inviteeMsg = text("You have been invited to ", GREEN).append(channel.getFriendlyName())
                .append(text(" by ", GREEN)).append(issuer.getFriendlyName());
        invitee.sendMessage(inviteeMsg);
        invitee.sendMessage(createInviteeAcceptDenyMessage(channel.getKey()));
        issuer.sendMessage(createIssuerInviteMessage(channel, invitee));
    }

    @Subcommand("join|accept")
    @Conditions("playerOnly")
    @CommandCompletion("@channels:invited")
    @CommandPermission("featherchat.channel.join")
    public void onJoin(@NotNull Player issuer, @NotNull @Flags("invited") UserChatChannel channel) {
        if (!plugin.getInvitations().removeInvitation(issuer, channel)) {
            throw new InvalidCommandArgument("You do not have a pending invite to the specified channel", false);
        }
        if (issuer.addChannel(channel)) {
            channel.sendMessage(issuer.getFriendlyName().append(text(" has joined the channel", GREEN)));
        }
        if (channel.addMember(issuer)) {
            issuer.sendMessage(text("You have joined ", GREEN).append(channel.getFriendlyName()));
        }
    }

    @Subcommand("deny|reject")
    @Conditions("playerOnly")
    @CommandCompletion("@channels:invited")
    @CommandPermission("featherchat.channel.deny")
    public void onDeny(@NotNull Player issuer, @NotNull @Flags("invited") UserChatChannel channel) {
        if (!plugin.getInvitations().removeInvitation(issuer, channel)) {
            throw new InvalidCommandArgument("You do not have a pending invite to the specified channel", false);
        }
        issuer.sendMessage(text("You have denied your invitation to ", RED).append(channel.getFriendlyName()));
    }

    @Subcommand("chat")
    @CommandCompletion("@channels")
    @CommandPermission("featherchat.channel.chat")
    public void onChat(@NotNull Player issuer, @NotNull UserChatChannel channel, @NotNull String message) {
        channel.sendMessage(issuer, text(message));
    }

    @Subcommand("displayname")
    @Conditions("playerOnly")
    @CommandAlias("nickname|nick")
    @CommandCompletion("@channels:owner")
    @CommandPermission("featherchat.channel.displayname")
    public void onDisplayName(@NotNull Player issuer, @NotNull @Flags("owned") UserChatChannel channel, @NotNull @Single @Conditions("channelName|charLimit:max=56") String displayName) {
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        channel.setDisplayName(serialized);
        issuer.sendMessage(text("Changed display name of channel to '", RED).append(serialized).append(text("'", RED)));
    }

    @NotNull
    private Component createIssuerInviteMessage(@NotNull UserChatChannel channel, @NotNull Player invitee) {
        return invitee.getFriendlyName().append(text(" has been invited to join ", GREEN)).append(channel.getFriendlyName());
    }

    @NotNull
    private Component createInviteeAcceptDenyMessage(@NotNull NamespacedChannelKey key) {
        Component accept = clickAndHoverCommand(text("Accept", GREEN, TextDecoration.BOLD), "featherchat channel join " + key);
        Component deny = clickAndHoverCommand(text("Deny", RED, TextDecoration.BOLD), "featherchat channel deny " + key);
        return text("[", DARK_GRAY).append(accept).append(text("]", DARK_GRAY))
                .append(text(" ")).append(text("[", DARK_GRAY)).append(deny).append(text("]", DARK_GRAY));
    }

    @NotNull
    private Component clickAndHoverCommand(@NotNull Component component, @NotNull String command) {
        return component.hoverEvent(HoverEvent.showText(text("Command: /" + command))).clickEvent(ClickEvent.runCommand("/"+command));
    }

}
