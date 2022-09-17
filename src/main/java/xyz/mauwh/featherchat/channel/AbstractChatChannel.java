package xyz.mauwh.featherchat.channel;

import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.NamespacedChannelKey;
import xyz.mauwh.featherchat.message.ChannelMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.messenger.ChatMessenger;

import java.util.*;

public abstract class AbstractChatChannel implements ChatChannel {

    private static final String DEFAULT_FORMAT = "<dark_gray>[<timestamp>]</dark_gray> <gray>[<channel_name>]</gray> <sender_name>: <message>";

    private final transient FeatherChat plugin;
    private final NamespacedChannelKey key;
    private final UUID uuid;
    private final String name;
    private Component displayName;
    private String format;
    private boolean consoleLogging;

    public AbstractChatChannel(@NotNull FeatherChat plugin, @NotNull String name) {
        this(plugin, UUID.randomUUID(), name);
    }

    public AbstractChatChannel(@NotNull FeatherChat plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, NamespacedChannelKey.featherchat(name), uuid, name);
    }

    protected AbstractChatChannel(@NotNull FeatherChat plugin, @NotNull NamespacedChannelKey key, @NotNull UUID uuid, @NotNull String name) {
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
    public boolean isMember(@NotNull UUID member) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void sendMessage(@NotNull ChatMessenger sender, @NotNull Component component) {
        ChannelMessage message = new ChannelMessage(this, sender, component);
        Component finalMessage = plugin.getMessageHandler().formatMessage(message);
        // noinspection OptionalGetWithoutIsPresent
        Audience receiving = plugin.getAudienceProvider().players().filterAudience(audience -> isMember(audience.get(Identity.UUID).get()));
        if (consoleLogging) {
            receiving = Audience.audience(receiving, plugin.getAudienceProvider().console());
        }
        receiving.sendMessage(sender, finalMessage);
    }

}
