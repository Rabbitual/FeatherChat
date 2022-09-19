package xyz.mauwh.featherchat.bukkit.command;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.contexts.ContextResolver;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.Collection;
import java.util.Locale;

public final class BukkitChatChannelContextResolver implements ContextResolver<UserChatChannel, BukkitCommandExecutionContext> {

    private final ChatChannels channelRepository;

    public BukkitChatChannelContextResolver(@NotNull ChatChannels channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    @NotNull
    public UserChatChannel getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String channelId = context.popFirstArg();
        String[] parts = channelId.split(":");

        UserChatChannel channel;
        if (parts.length == 1) {
            String name = parts[0].toLowerCase(Locale.ROOT);
            Collection<? extends UserChatChannel> userChannels = channelRepository.filterByName(name);
            if (userChannels.size() > 1) {
                throw new InvalidCommandArgument("Channel name '" + parts[0] + "' is ambiguous - please try using the channel ID (<owner>:<channel name>)", false);
            } else if (userChannels.size() == 0) {
                throw new InvalidCommandArgument("Unable to find channel '" + parts[0] + "'", false);
            }
            channel = userChannels.toArray(UserChatChannel[]::new)[0];
        } else {
            NamespacedChannelKey key = new NamespacedChannelKey(parts[0], parts[1]);
            channel = channelRepository.resolveByKey(key);
            if (channel == null) {
                throw new InvalidCommandArgument("Unable to find channel '" + key + "'", false);
            }
        }

        String value = context.getAnnotationValue(Conditions.class);
        if (value != null && value.equals("isMember") && !channel.isMember(context.getIssuer().getUniqueId())) {
            throw new InvalidCommandArgument("Unable to find channel '" + channelId.toLowerCase(Locale.ROOT) + "'", false);
        }

        return channel;
    }

}
