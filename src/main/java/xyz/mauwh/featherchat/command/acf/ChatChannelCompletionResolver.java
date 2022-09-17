package xyz.mauwh.featherchat.command.acf;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.NamespacedChannelKey;
import xyz.mauwh.featherchat.channel.ChatChannelRepository;
import xyz.mauwh.featherchat.channel.UserChatChannel;
import xyz.mauwh.featherchat.messenger.ChatMessenger;
import xyz.mauwh.featherchat.messenger.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatChannelCompletionResolver implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    private final ChatChannelRepository channelRepository;

    public ChatChannelCompletionResolver(@NotNull ChatChannelRepository channelRepository) {
        Objects.requireNonNull(channelRepository, "null channel repository");
        this.channelRepository = channelRepository;
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        Player player = ChatMessenger.player(context.getPlayer());
        Set<? extends UserChatChannel> channels = channelRepository.filterByParticipant(player);
        Set<String> matches = new HashSet<>();
        channels.forEach(channel -> {
            NamespacedChannelKey key = channel.getKey();
            if (StringUtil.startsWithIgnoreCase(key.toString(), context.getInput())) {
                matches.add(key.toString());
            } else if (StringUtil.startsWithIgnoreCase(key.getKey(), context.getInput())) {
                matches.add(key.getKey());
            }
        });
        return matches;
    }

}
