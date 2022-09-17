package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.ComponentPersistentDataType;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.channel.ChatChannel;
import xyz.mauwh.featherchat.channel.UserChatChannel;

import java.util.*;

public final class ChatMessengerPlayerImpl extends ChatMessengerImpl implements Player {

    private final UUID uuid;
    private final Set<UUID> channels;

    public ChatMessengerPlayerImpl(@NotNull FeatherChat plugin, @NotNull UUID uuid, @NotNull String name) {
        super(plugin, Bukkit.getPlayer(uuid), name);
        this.uuid = uuid;
        this.channels = new HashSet<>();
    }

    public ChatMessengerPlayerImpl(@NotNull FeatherChat plugin, @NotNull org.bukkit.entity.Player handle) {
        super(plugin, handle);
        this.uuid = handle.getUniqueId();
        this.channels = new HashSet<>();
    }

    @Override
    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        setDisplayName(displayName, true);
    }

    public void setDisplayName(@Nullable Component displayName, boolean update) {
        super.setDisplayName(displayName);

        org.bukkit.entity.Player player = getHandle();
        if (player == null) {
            return;
        }

        PersistentDataContainer dataContainer = getHandle().getPersistentDataContainer();
        NamespacedKey displayNameKey = new NamespacedKey(plugin, "displayname");
        if (displayName == null) {
            dataContainer.remove(displayNameKey);
            return;
        }
        dataContainer.set(displayNameKey, ComponentPersistentDataType.get(), displayName);
        player.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayName));
        if (update) {
            plugin.getMessengers().update(this);
        }
    }

    @Override
    @Nullable
    public org.bukkit.entity.Player getHandle() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    @NotNull
    public Set<UUID> getChannels() {
        return Set.copyOf(channels);
    }

    public void setChannels(@NotNull Set<UUID> channels) {
        this.channels.clear();
        this.channels.addAll(channels);
    }

    public void addChannel(@NotNull ChatChannel channel) {
        this.channels.add(channel.getUUID());
        plugin.getMessengers().update(this);
    }

    public void removeChannel(@NotNull ChatChannel channel) {
        this.channels.remove(channel.getUUID());
        plugin.getMessengers().update(this);
    }

    public void removeChannel(@NotNull UUID channel) {
        this.channels.remove(channel);
        plugin.getMessengers().update(this);
    }

    @Override
    public void validateChannels() {
        Map<UUID, UserChatChannel> channels = plugin.getChannelRepository().resolveByUUIDs(this.getChannels());
        Iterator<Map.Entry<UUID, UserChatChannel>> iter = channels.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, UserChatChannel> entry = iter.next();
            if (entry.getValue() == null || !entry.getValue().isMember(uuid)) {
                iter.remove();
                removeChannel(entry.getKey());
            }
        }
    }

    @Override
    public boolean isOnline() {
        return getHandle() == null;
    }

    @Override
    @NotNull
    public String toString() {
        return String.format("ChatMessengerPlayerImpl{uuid=%s, channels=%s, %s}", uuid, channels, super.toString());
    }

}
