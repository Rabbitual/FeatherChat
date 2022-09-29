package xyz.mauwh.featherchat.command.acf;

import co.aikar.commands.CommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitation;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class FeatherChatContextResolvers {

    private final ChatMessengers<?, ? extends ChatMessenger, ? extends Player> messengers;
    private final ChatChannels channelRepository;
    private final ChannelInvitations invitations;

    public FeatherChatContextResolvers(@NotNull FeatherChatPlugin plugin) {
        this.messengers = plugin.getMessengers();
        this.channelRepository = plugin.getChannels();
        this.invitations = plugin.getInvitations();
    }

    @NotNull
    public ChatMessenger getMessenger(CommandExecutionContext<?, ?> context) throws InvalidCommandArgument {
        return messengers.getBySender(context.getIssuer().getIssuer());
    }

    @NotNull
    public Player getPlayer(@NotNull CommandExecutionContext<?, ?> context) throws InvalidCommandArgument {
        if (!context.hasFlag("other")) {
            return messengers.getByUUID(context.getIssuer().getUniqueId());
        }
        Player player = messengers.getByName(context.popFirstArg());
        if (player == null) {
            throw new InvalidCommandArgument("Player could not be found");
        }
        return player;
    }

    @NotNull
    public UserChatChannel getUserChatChannel(@NotNull CommandExecutionContext<?, ?> context) throws InvalidCommandArgument {
        String channelId = context.popFirstArg();
        String[] parts = channelId.split(":");

        ChatMessenger messenger = messengers.getBySender(context.getIssuer().getIssuer());
        if (!messenger.isPlayer()) {
            throw new InvalidCommandArgument("Only players may use this command");
        }
        Player player = (Player)messenger;

        Set<UserChatChannel> channels;
        if (context.hasFlag("invited")) {
            channels = invitations.getInvitations(player).stream().map(ChannelInvitation::getChannel).collect(Collectors.toSet());
        } else if (context.hasFlag("owned")) {
            channels = channelRepository.filterByOwner(context.getIssuer().getUniqueId());
        } else {
            channels = channelRepository.filterByName(parts[0].toLowerCase(Locale.ROOT));
        }

        if (parts.length == 1) {
            if (channels.size() > 1) {
                throw new InvalidCommandArgument("Channel name '" + parts[0] + "' is ambiguous - please try using the channel ID (<owner>:<channel name>)", false);
            } else if (channels.size() == 0) {
                throw new InvalidCommandArgument("Unable to find channel '" + parts[0] + "'", false);
            }
            return channels.iterator().next();
        }

        NamespacedChannelKey key = new NamespacedChannelKey(parts[0], parts[1]);
        UserChatChannel channel = channelRepository.resolveByKey(key);
        if (channel == null) {
            throw new InvalidCommandArgument("Unable to find channel '" + key + "'", false);
        }
        return channel;
    }

}
