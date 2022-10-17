package xyz.mauwh.featherchat.bukkit.messenger;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.messenger.AbstractChatMessenger;
import xyz.mauwh.featherchat.messenger.PlayerAccessible;

import java.util.*;

public final class BukkitPlayerImpl extends AbstractChatMessenger implements PlayerAccessible, Player {

    private final FeatherChatBukkit plugin;
    private final UUID uuid;
    private final Set<UUID> channels;
    private org.bukkit.entity.Player handle;

    public BukkitPlayerImpl(@NotNull FeatherChatBukkit plugin, @NotNull UUID uuid, @NotNull String name) {
        super(name);
        this.plugin = plugin;
        this.uuid = uuid;
        this.channels = new HashSet<>();
    }

    public BukkitPlayerImpl(@NotNull FeatherChatBukkit plugin, @NotNull org.bukkit.entity.Player handle) {
        this(plugin, handle.getUniqueId(), handle.getName());
        this.handle = handle;
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
        if (player == null || displayName == null) {
            return;
        }
        player.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayName));
        if (update) {
            plugin.getMessengers().update(this);
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public org.bukkit.entity.Player getHandle() {
        return handle != null ? null : Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return isOnline() && Objects.requireNonNull(getHandle()).hasPermission(permission);
    }

    public void setChannels(@NotNull Collection<UUID> channels) {
        this.channels.clear();
        this.channels.addAll(channels);
    }

    @Override
    @NotNull
    public Set<UUID> getChannels() {
        return Set.copyOf(channels);
    }

    @Override
    public boolean addChannel(@NotNull ChatChannel channel) {
        boolean result = channels.add(channel.getUUID());
        plugin.getMessengers().update(this);
        return result;
    }

    @Override
    public boolean removeChannel(@NotNull ChatChannel channel) {
        boolean result = channels.remove(channel.getUUID());
        plugin.getMessengers().update(this);
        return result;
    }

    @Override
    public void sendMessage(@Nullable ChatMessenger sender, @NotNull Component message) {
        if (this.handle == null) {
            return;
        }
        Audience audience = plugin.getAudienceProvider().sender(handle);
        if (sender == null) {
            audience.sendMessage(message);
        } else {
            audience.sendMessage(sender, message);
        }
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        sendMessage(null, message);
    }

    @Override
    public void validateChannels() {
        Map<UUID, UserChatChannel> channels = plugin.getChannels().resolveByUUIDs(this.getChannels());
        Iterator<Map.Entry<UUID, UserChatChannel>> iter = channels.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, UserChatChannel> entry = iter.next();
            if (entry.getValue() == null || !entry.getValue().isMember(this)) {
                iter.remove();
                removeChannel(entry.getValue());
            }
        }
    }

    @Override
    public void kickPlayer(@Nullable String message) {
        getHandle();
        if (handle != null) {
            handle.kickPlayer(message);
        }
    }

    @Override
    public boolean isOnline() {
        return getHandle() != null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && uuid.equals(((BukkitPlayerImpl)o).uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), uuid);
    }

    @Override
    @NotNull
    public String toString() {
        return String.format("BukkitPlayerImpl{uuid=%s, channels=%s, %s}", uuid, channels, super.toString());
    }

    @Override
    @NotNull
    public Identity identity() {
        return Identity.identity(uuid);
    }

}
