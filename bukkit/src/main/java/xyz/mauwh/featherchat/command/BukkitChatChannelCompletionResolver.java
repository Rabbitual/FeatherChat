package xyz.mauwh.featherchat.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.FeatherChatBukkit;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.messenger.BukkitPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BukkitChatChannelCompletionResolver implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    private final FeatherChatBukkit plugin;
    private final ChatChannels channels;

    public BukkitChatChannelCompletionResolver(@NotNull FeatherChatBukkit plugin) {
        Objects.requireNonNull(plugin, "null plugin");
        this.plugin = plugin;
        this.channels = plugin.getChannels();
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        BukkitPlayer player = (BukkitPlayer)plugin.getMessengers().getBySender(context.getPlayer());
        Set<? extends UserChatChannel> channels = this.channels.filterByParticipant(player);
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
