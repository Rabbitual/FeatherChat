package xyz.mauwh.featherchat.bukkit.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.api.message.ChannelMessage;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PlayerChatListener implements Listener {

    private final FeatherChatBukkit plugin;
    private final ChatChannels channels;

    public PlayerChatListener(@NotNull FeatherChatBukkit plugin) {
        this.plugin = plugin;
        this.channels = plugin.getChannels();
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        handleChat(event);
    }

    @EventHandler
    public void onPlayerChatPreview(@NotNull AsyncPlayerChatPreviewEvent event) {
        handleChat(event);
    }

    private void handleChat(@NotNull AsyncPlayerChatEvent event) {
        if (event.getMessage().charAt(0) != '@') {
            return;
        } else if (event.getMessage().length() == 1) {
            setBadResult(event, ChatColor.RED + "You must specify a channel to send a message to");
            return;
        }

        event.setCancelled(!(event instanceof AsyncPlayerChatPreviewEvent));
        String[] args = event.getMessage().split(" ");
        if (args.length <= 1) {
            setBadResult(event, ChatColor.RED + "You must specify the message to send to this channel");
            return;
        }

        String channelId = args[0].substring(1);
        Player player = (Player)plugin.getMessengers().getBySender(event.getPlayer());
        Objects.requireNonNull(player);

        Optional<NamespacedChannelKey> key = NamespacedChannelKey.fromString(channelId);
        UserChatChannel channel = null;
        if (key.isPresent()) {
            channel = channels.resolveByKey(key.get());
        }

        if (channel == null) {
            try {
                channel = resolveByName(channelId, player);
            } catch (IllegalArgumentException err1) {
                setBadResult(event, ChatColor.RED + err1.getMessage());
                return;
            }
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (event instanceof AsyncPlayerChatPreviewEvent) {
            ChannelMessage channelMessage = new ChannelMessage(channel, player, Component.text(message));
            String format = plugin.getMessageHandler().formatPreview(channelMessage);
            event.setMessage(message);
            event.setFormat(format);
            return;
        }

        channel.sendMessage(player, Component.text(message));
    }

    private void setBadResult(@NotNull AsyncPlayerChatEvent event, @NotNull String message) {
        if (event instanceof AsyncPlayerChatPreviewEvent) {
            event.setFormat("%2$s");
            event.setMessage(message);
        } else {
            event.getPlayer().sendMessage(message);
            event.setCancelled(true);
        }
    }

    @NotNull
    private UserChatChannel resolveByName(@NotNull String channelId, @NotNull Player player) throws IllegalArgumentException {
        Set<UserChatChannel> filtered = channels.filterByName(channelId, channels.filterByParticipant(player));
        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("No channel found with name or ID '" + channelId + "'");
        } else if (filtered.size() > 1) {
            throw new IllegalArgumentException("Channel name '" + channelId + "' is ambiguous (" + filtered.size() + " matches)");
        }
        return filtered.iterator().next();
    }

}
