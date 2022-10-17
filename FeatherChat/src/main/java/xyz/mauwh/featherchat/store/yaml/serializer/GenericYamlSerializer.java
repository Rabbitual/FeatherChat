package xyz.mauwh.featherchat.store.yaml.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;

import java.util.*;
import java.util.logging.Logger;

public class GenericYamlSerializer {

    public static final MiniMessage COLORED;
    public static final MiniMessage COLORED_DECORATED;
    private final Logger logger;

    static {
        COLORED = MiniMessage.builder().tags(StandardTags.color()).build();
        COLORED_DECORATED = MiniMessage.builder().tags(TagResolver.resolver(StandardTags.color(), StandardTags.decorations())).build();
    }

    public GenericYamlSerializer() {
        this.logger = Logger.getLogger("FeatherChatSerializer");
    }

    public void checkRequiredPlayerValuesNonNull(@NotNull Map<String, Object> values) throws NullPointerException {
        Objects.requireNonNull(values.get("uuid"), "null uuid");
        Objects.requireNonNull(values.get("key"), "null key");
    }

    public void checkRequiredChannelValuesNonNull(@NotNull Map<String, Object> values) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(values.get("uuid"), "null uuid");
        Objects.requireNonNull(values.get("name"), "null name");
        Objects.requireNonNull(values.get("key"), "null key");
        Objects.requireNonNull(values.get("members"), "null members");
    }

    public void checkRequiredChannelMemberValuesNonNull(@NotNull Map<String, Object> values) throws NullPointerException {
        Objects.requireNonNull(values.get("channel-uuid"), "null channel uuid");
        Objects.requireNonNull(values.get("player-uuid"), "null player uuid");
    }

    @NotNull
    public String serializeComponent(@NotNull Component component, @NotNull MiniMessage componentSerializer) {
        return componentSerializer.serialize(component);
    }

    @NotNull
    public UUID deserializeUUID(@NotNull Map<String, Object> values, @NotNull String path) throws NullPointerException, IllegalArgumentException {
        Object value = values.get(path);
        Objects.requireNonNull(value, "null uuid");
        return UUID.fromString(value.toString());
    }

    @NotNull
    public List<UUID> deserializeUUIDList(@NotNull Map<String, Object> values, @NotNull String path) throws NullPointerException, IllegalArgumentException {
        Object uuids = values.get(path);
        if (!(uuids instanceof List<?>)) {
            throw new IllegalArgumentException("members is not a list");
        }
        List<UUID> members = new ArrayList<>();
        for (Object o : (List<?>)uuids) {
            try {
                members.add(UUID.fromString(o.toString()));
            } catch (IllegalArgumentException err) {
                logger.warning("Unable to parse invalid UUID from list [" + o + "]");
            }
        }
        return members;
    }

    @NotNull
    public NamespacedChannelKey deserializeChannelKey(@NotNull Map<String, Object> values, @NotNull String path) throws NullPointerException, IllegalArgumentException {
        Object key = values.get(path);
        Objects.requireNonNull(key, "null key");
        String[] parts = key.toString().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid key '" + key + "'");
        }
        return new NamespacedChannelKey(parts[0], parts[1]);
    }

    @Nullable
    public Component deserializeComponent(@NotNull Map<String, Object> values, @NotNull String path,
                                          @NotNull MiniMessage componentSerializer, @NotNull TagResolver... resolvers) {
        Object displayName = values.get(path);
        if (displayName == null) {
            return null;
        }
        return componentSerializer.deserialize(displayName.toString(), resolvers);
    }

}
