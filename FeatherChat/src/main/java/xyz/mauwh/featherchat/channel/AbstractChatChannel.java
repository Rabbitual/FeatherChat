package xyz.mauwh.featherchat.channel;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.message.ChannelMessage;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractChatChannel implements ChannelAccessible, ChatChannel {

    private static final String DEFAULT_FORMAT = "<dark_gray>[<timestamp>]</dark_gray> <gray>[<channel_name>]</gray> <sender_name>: <message>";

    protected final FeatherChatAccessible plugin;
    private final NamespacedChannelKey key;
    private final UUID uuid;
    private final String name;
    private Component displayName;
    private String format;
    private boolean consoleLogging;

    public AbstractChatChannel(@NotNull FeatherChatAccessible plugin, @NotNull String name) {
        this(plugin, UUID.randomUUID(), name);
    }

    public AbstractChatChannel(@NotNull FeatherChatAccessible plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, NamespacedChannelKey.featherchat(name), uuid, name);
    }

    protected AbstractChatChannel(@NotNull FeatherChatAccessible plugin, @NotNull NamespacedChannelKey key, @NotNull UUID uuid, @NotNull String name) {
        this.plugin = plugin;
        this.key = key;
        this.uuid = uuid;
        this.name = name;
        this.format = DEFAULT_FORMAT;
    }

    @NotNull
    public NamespacedChannelKey getKey() {
        return key;
    }

    @Override
    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public Optional<Component> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @Override
    @NotNull
    public Component getFriendlyName() {
        return displayName != null ? displayName : Component.text(name);
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    @Override
    @NotNull
    public String getMessageFormat() {
        return format;
    }

    @Override
    public void setMessageFormat(@NotNull String format) {
        this.format = format;
    }

    @Override
    @NotNull
    public Set<UUID> getMembers() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void setMembers(@NotNull Set<UUID> members) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean isMember(@NotNull UUID member) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean isMember(@NotNull Player<?> member) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean addMember(@NotNull Player<?> member) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean removeMember(@NotNull Player<?> member) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void sendMessage(@NotNull ChatMessenger<?> sender, @NotNull Component component) {
        ChannelMessage message = new ChannelMessage(this, sender, component);
        Component finalMessage = plugin.getMessageHandler().formatMessage(message);
        Audience receiving = plugin.getAdventure().players().filterAudience(audience ->
                isMember(plugin.getMessengers().getByUUID(audience.get(Identity.UUID).orElseThrow()))
        );
        if (consoleLogging) {
            receiving = Audience.audience(receiving, plugin.getAdventure().console());
        }
        receiving.sendMessage(sender, finalMessage);
    }

}
