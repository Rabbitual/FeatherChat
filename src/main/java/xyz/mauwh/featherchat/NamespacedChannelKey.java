package xyz.mauwh.featherchat;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.messenger.ChatMessenger;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class  NamespacedChannelKey {

    private static final Pattern VALID_CHARACTERS = Pattern.compile("[a-z0-9_-]+");

    private final String namespace;
    private final String key;

    public NamespacedChannelKey(@NotNull ChatMessenger owner, @NotNull String channelName) {
        this(owner.getName(), channelName);
    }

    public NamespacedChannelKey(@NotNull String namespace, @NotNull String channelName) {
        Preconditions.checkNotNull(namespace, "Owner cannot be null");
        Preconditions.checkNotNull(channelName, "Channel name cannot be null");
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.key = channelName.toLowerCase(Locale.ROOT);
        Preconditions.checkArgument(VALID_CHARACTERS.matcher(this.namespace).matches(), "Invalid namespace. Must be [a-z0-9_-]: %s", this.namespace);
        Preconditions.checkArgument(VALID_CHARACTERS.matcher(this.key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", this.key);
        Preconditions.checkArgument(toString().length() < 48, "NamespacedChannelKey must be less than 48 characters (%s)", toString());
    }

    @NotNull
    public static NamespacedChannelKey featherchat(@NotNull String channelName) {
        return new NamespacedChannelKey("featherchat", channelName);
    }

    @NotNull
    public String getNamespace() {
        return namespace;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public static Optional<NamespacedChannelKey> fromString(@NotNull String key) {
        String[] parts = key.split(":");
        return Optional.ofNullable(parts.length == 2 ? new NamespacedChannelKey(parts[0], parts[1]) : null);
    }

    @NotNull
    public String toString() {
        return namespace + ":" + key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NamespacedChannelKey other = (NamespacedChannelKey) obj;
        return this.namespace.equals(other.namespace) && this.key.equals(other.key);
    }

    public int hashCode() {
        return Objects.hash(this.namespace, this.key);
    }

}
