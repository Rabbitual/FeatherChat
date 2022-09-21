package xyz.mauwh.featherchat.command.acf;

import co.aikar.commands.*;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class FeatherChatCommandCompletionHandler {

    private final FeatherChatAccessible plugin;
    private final ChatChannels channels;

    public FeatherChatCommandCompletionHandler(@NotNull FeatherChatAccessible plugin) {
        Objects.requireNonNull(plugin, "null plugin");
        this.plugin = plugin;
        this.channels = plugin.getChannels();
    }

    @NotNull
    public Collection<String> getCompletions(CommandCompletionContext<?> context) throws InvalidCommandArgument {
        Player<?> player = plugin.getMessengers().getByUUID(context.getIssuer().getUniqueId());
        Set<? extends UserChatChannel> channels = this.channels.filterByParticipant(player);
        Set<String> matches = new HashSet<>();
        channels.forEach(channel -> {
            NamespacedChannelKey key = channel.getKey();
            if (startsWithIgnoreCase(key.toString(), context.getInput())) {
                matches.add(key.toString());
            } else if (startsWithIgnoreCase(key.getKey(), context.getInput())) {
                matches.add(key.getKey());
            }
        });
        return matches;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean startsWithIgnoreCase(@NotNull final String string, @NotNull final String prefix) throws IllegalArgumentException, NullPointerException {
        if (string == null) {
            throw new IllegalArgumentException("Cannot check a null string for a match");
        }
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

}
