package xyz.mauwh.featherchat.channel;

import org.bukkit.Bukkit;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.NamespacedChannelKey;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.messenger.ChatMessenger;
import xyz.mauwh.featherchat.messenger.ChatMessengerPlayerImpl;
import xyz.mauwh.featherchat.messenger.Player;
import xyz.mauwh.featherchat.store.yaml.YamlChatChannelDAO;

import java.util.*;
import java.util.stream.Collectors;

public final class ChatChannelRepository {

    private final FeatherChat plugin;
    private final ChatChannel debugChannel;
    private final Map<UUID, UserChatChannel> uuid2channel;
    private final Map<NamespacedChannelKey, UserChatChannel> key2channel;
    private final YamlChatChannelDAO chatChannelDAO;

    public ChatChannelRepository(@NotNull FeatherChat plugin) {
        this.plugin = plugin;
        this.debugChannel = new AbstractChatChannel(plugin, "debug") {
            @Override
            public boolean isMember(@NotNull UUID member) {
                org.bukkit.entity.Player player = Bukkit.getPlayer(member);
                return player != null && (player.hasPermission("featherchat.debug") || player.isOp());
            }
        };
        this.uuid2channel = new HashMap<>();
        this.key2channel = new HashMap<>();
        this.chatChannelDAO = new YamlChatChannelDAO(plugin);
    }

    /**
     * Creates a new BasicChatChannel with the specified owner and name.
     * @param owner - the owner of the chat channel to be created
     * @param name - the name of the chat channel to be created
     * @return the newly created chat channel
     */
    @NotNull
    public ChatChannel createChannel(@NotNull Player owner, @NotNull String name) {
        NamespacedChannelKey key = new NamespacedChannelKey(owner, name);
        if (key2channel.get(key) != null) {
            throw new IllegalArgumentException("Channel with key '" + key + "' already exists");
        }

        UserChatChannel channel = new UserChatChannelImpl(plugin, UUID.randomUUID(), key, owner.getUUID(), name);
        uuid2channel.put(channel.getUUID(), channel);
        key2channel.put(key, channel);
        chatChannelDAO.create(channel);
        ((ChatMessengerPlayerImpl)owner).addChannel(channel);
        return channel;
    }

    public void updateChannel(@NotNull UserChatChannel channel) {
        chatChannelDAO.update(channel);
    }

    public void dissolveChannel(@NotNull UserChatChannel channel) {
        ((UserChatChannelImpl)channel).sendDissolutionMessage();
        uuid2channel.remove(channel.getUUID());
        key2channel.remove(channel.getKey());
        chatChannelDAO.delete(channel);
        ((ChatMessengerPlayerImpl)ChatMessenger.player(channel.getOwner())).removeChannel(channel);
    }

    /**
     * Gets a singleton channel dedicated for debugging purposes.
     * @return the debug chat channel
     */
    @NotNull
    public ChatChannel getDebugChannel() {
        return debugChannel;
    }

    @Nullable
    public UserChatChannel resolveByUUID(@NotNull UUID uuid) {
        try {
            return uuid2channel.computeIfAbsent(uuid, uuid1 -> {
                UserChatChannel channel = chatChannelDAO.read(uuid1);
                key2channel.put(channel.getKey(), channel);
                return channel;
            });
        } catch (DataEntityAccessException err) {
            return null;
        }
    }

    @NotNull
    public Map<UUID, UserChatChannel> resolveByUUIDs(@NotNull Iterable<UUID> uuids) {
        Map<UUID, UserChatChannel> channels = new HashMap<>();
        uuids.forEach(uuid -> {
            UserChatChannel channel = resolveByUUID(uuid);
            channels.put(uuid, channel);
            if (channel != null) {
                uuid2channel.putIfAbsent(uuid, channel);
                key2channel.putIfAbsent(channel.getKey(), channel);
            }
        });
        return channels;
    }

    @Nullable
    public UserChatChannel resolveByKey(@NotNull NamespacedChannelKey key) {
        return key2channel.get(key);
    }

    @NotNull
    public Set<UserChatChannel> getUserChannels() {
        return Set.copyOf(key2channel.values());
    }

    @NotNull
    public Set<UserChatChannel> filterByName(@NotNull String name) {
        return key2channel.values().stream().filter(channel -> {
            NamespacedChannelKey key = channel.getKey();
            return StringUtil.startsWithIgnoreCase(key.getKey(), name) || StringUtil.startsWithIgnoreCase(key.toString(), name);
        }).collect(Collectors.toSet());
    }

    public static Set<UserChatChannel> filterByName(@NotNull String name, Set<UserChatChannel> channels) {
        channels.removeIf(channel -> {
            NamespacedChannelKey key = channel.getKey();
            return !StringUtil.startsWithIgnoreCase(key.getKey(), name) && !StringUtil.startsWithIgnoreCase(key.toString(), name);
        });
        return channels;
    }

    @NotNull
    public Set<UserChatChannel> filterByOwner(@NotNull Player owner) {
        return key2channel.values().stream().filter(channel -> channel.getOwner().equals(owner.getUUID())).collect(Collectors.toSet());
    }

    @NotNull
    public Set<UserChatChannel> filterByOwner(@NotNull UUID owner) {
        return key2channel.values().stream().filter(channel -> channel.getOwner().equals(owner)).collect(Collectors.toSet());
    }

    @NotNull
    public Set<UserChatChannel> filterByParticipant(@NotNull Player participant) {
        return participant.getChannels().stream().map(this::resolveByUUID).collect(Collectors.toSet());
    }

    @NotNull
    public Set<UserChatChannel> filterByParticipant(@NotNull UUID participant) {
        return ChatMessenger.player(participant).getChannels().stream().map(this::resolveByUUID).collect(Collectors.toSet());
    }

}
