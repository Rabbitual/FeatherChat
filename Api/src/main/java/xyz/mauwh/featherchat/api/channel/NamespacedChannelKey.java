package xyz.mauwh.featherchat.api.channel;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class NamespacedChannelKey {

    private static final Pattern VALID_CHARACTERS = Pattern.compile("[a-z0-9_-]+");

    private final String namespace;
    private final String key;

    public NamespacedChannelKey(@NotNull ChatMessenger owner, @NotNull String channelName) {
        this(owner.getName(), channelName);
    }

    public NamespacedChannelKey(@NotNull String namespace, @NotNull String channelName) {
        Objects.requireNonNull(namespace, "Owner cannot be null");
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.key = channelName.toLowerCase(Locale.ROOT);
        if (!VALID_CHARACTERS.matcher(this.namespace).matches()) {
            throw new IllegalArgumentException("Invalid namespace. Must be [a-z0-9_-] (" + this.namespace + ")");
        } else if (!VALID_CHARACTERS.matcher(this.key).matches()) {
            throw new IllegalArgumentException("Invalid key. Must be [a-z0-9/._-] (" + this);
        } else if (toString().length() > 48) {
            throw new IllegalArgumentException("NamespacedChannelKey must be less than 48 characters (" + this + ")");
        }
    }

    /**
     * Creates a NamespacedChannelKey in the "featherchat" namespace
     * @param channelName - The channel name
     * @return A new NamespacedChannelKey in the "featherchat" namespace
     */
    @NotNull
    public static NamespacedChannelKey featherchat(@NotNull String channelName) {
        return new NamespacedChannelKey("featherchat", channelName);
    }

    /**
     * Gets the namespace of this NamespacedChannelKey
     * @return this NamespacedChannelKey's namespace
     */
    @NotNull
    public String getNamespace() {
        return namespace;
    }

    /**
     * Gets the key of this NamespacedChannelKey, typically the channel name
     * @return this NamespacedChannelKey's key
     */
    @NotNull
    public String getKey() {
        return key;
    }

    /**
     * Creates a NamespacedChannelKey from the provided string key.
     * @param key - The string key to create a NamespacedChannelKey from
     * @return An optional containing the new NamespacedChannelKey, or empty if the key is invalid
     */
    @NotNull
    public static Optional<NamespacedChannelKey> fromString(@NotNull String key) {
        String[] parts = key.split(":");
        if (parts.length != 2) {
            return Optional.empty();
        }
        try {
            return Optional.of(new NamespacedChannelKey(parts[0], parts[1]));
        } catch (IllegalArgumentException err) {
            return Optional.empty();
        }
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
