package xyz.mauwh.featherchat.bukkit.messenger;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

import java.util.Objects;
import java.util.Optional;

public class BukkitChatMessenger implements ChatMessenger {

    protected final FeatherChatBukkit plugin;
    protected String name;
    private Component displayName;
    private final CommandSender handle;

    public BukkitChatMessenger(@NotNull FeatherChatBukkit plugin, @Nullable CommandSender handle) {
        Objects.requireNonNull(plugin, "null plugin");
        this.plugin = plugin;
        this.name = handle != null ? handle.getName() : null;
        this.handle = handle;
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
        return getDisplayName().orElse(Component.text(name));
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public CommandSender getHandle() {
        return handle;
    }

    @Override
    public boolean isPlayer() {
        return handle instanceof Player;
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
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
    public boolean hasPermission(String permission) {
        return getHandle() != null && getHandle().hasPermission(permission);
    }

    @Override
    @NotNull
    public Identity identity() {
        return Identity.nil();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final BukkitChatMessenger other = (BukkitChatMessenger)o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("ChatMessengerImpl(handle=%s, name=%s, displayName=%s)", handle, name, displayName);
    }

}
