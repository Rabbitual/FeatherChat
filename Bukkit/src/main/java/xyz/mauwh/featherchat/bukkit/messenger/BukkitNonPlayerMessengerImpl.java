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
import xyz.mauwh.featherchat.messenger.AbstractChatMessenger;

import java.util.Objects;

public class BukkitNonPlayerMessengerImpl extends AbstractChatMessenger {

    protected final FeatherChatBukkit plugin;
    private final CommandSender handle;

    public BukkitNonPlayerMessengerImpl(@NotNull FeatherChatBukkit plugin, @NotNull CommandSender handle) {
        super(handle.getName());
        Objects.requireNonNull(plugin, "null plugin");
        this.plugin = plugin;
        this.handle = handle;
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
        final BukkitNonPlayerMessengerImpl other = (BukkitNonPlayerMessengerImpl)o;
        return getName().equals(other.name);
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
