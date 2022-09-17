package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.FeatherChat;

import java.util.Objects;
import java.util.Optional;

public class ChatMessengerImpl implements ChatMessenger {

    protected final FeatherChat plugin;
    private final CommandSender handle;
    private final String name;
    private Component displayName;

    public ChatMessengerImpl(@NotNull FeatherChat plugin, @NotNull CommandSender handle) {
        this(plugin, handle, handle.getName());
    }

    ChatMessengerImpl(@NotNull FeatherChat plugin, @Nullable CommandSender handle, @NotNull String name) {
        Objects.requireNonNull(plugin, "null plugin");
        Objects.requireNonNull(name, "null name");
        this.plugin = plugin;
        this.name = name;
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
    public CommandSender getHandle() {
        return handle;
    }

    @Override
    public boolean isPlayer() {
        return false;
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
    @NotNull
    public Identity identity() {
        return Identity.nil();
    }

    @NotNull
    public FeatherChat getPlugin() {
        return plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final ChatMessengerImpl other = (ChatMessengerImpl)o;
        return handle.equals(other.handle) && name.equals(other.name);
    }

    @Override
    public String toString() {
        return String.format("ChatMessengerImpl(handle=%s, name=%s, displayName=%s)", handle, name, displayName);
    }

}
