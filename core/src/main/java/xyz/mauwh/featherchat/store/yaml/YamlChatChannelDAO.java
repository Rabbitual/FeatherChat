package xyz.mauwh.featherchat.store.yaml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.channel.UserChatChannelImpl;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;
import xyz.mauwh.featherchat.store.DataAccessObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class YamlChatChannelDAO implements DataAccessObject<UserChatChannel, UUID> {

    private final FeatherChatAccessible plugin;
    private final File channelsDir;

    public YamlChatChannelDAO(@NotNull FeatherChatAccessible plugin) {
        this.plugin = plugin;
        this.channelsDir = new File(plugin.getDataFolder(), "channels");
    }

    @Override
    public void create(@NotNull UserChatChannel channel) throws DataEntityAccessException {
        if (!(channel instanceof UserChatChannelImpl)) {
            throw new IllegalArgumentException("Channel must be of type BasicChatChannel");
        }
        channelsDir.mkdirs();
        File file = ymlFile(channel);
        try {
            if (file.createNewFile()) {
                this.update(channel);
            }
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to create channel", err);
        }
    }

    @Override
    @NotNull
    public UserChatChannel read(@NotNull UUID channelUUID) throws DataEntityAccessException {
        File file = ymlFile(channelUUID);
        if (!file.exists()) {
            throw new DataEntityAccessException("Attempted to read channel which does not exist");
        }
        try (InputStream input = new FileInputStream(file)) {
            return deserialize(plugin, new Yaml().load(input));
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to read channel data", err);
        }
    }

    @Override
    public void update(@NotNull UserChatChannel channel) throws DataEntityAccessException {
        File file = new File(channelsDir, channel.getUUID() + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Attempted to non-existent channel data file (" + channel.getUUID() + ".yml)");
        }
        try (FileWriter writer = new FileWriter(file)) {
            new Yaml().dump(serialize(channel), writer);
        } catch (IOException err) {
            throw new DataEntityAccessException("Unexpected exception occurred while attempting to save updated config", err);
        }
    }

    @Override
    public void delete(@NotNull UserChatChannel channel) {
        ymlFile(channel).delete();
    }

    @NotNull
    public static Map<String, Object> serialize(@NotNull UserChatChannel channel) {
        Map<String, Object> values = new HashMap<>();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        values.put("key", channel.getKey().toString());
        values.put("uuid", channel.getUUID().toString());
        values.put("name", channel.getName());
        channel.getDisplayName().ifPresent(displayName -> values.put("displayName", miniMessage.serialize(displayName)));
        values.put("owner", channel.getOwner().toString());
        values.put("messageFormat", channel.getMessageFormat());
        return values;
    }

    @NotNull
    public static UserChatChannel deserialize(@NotNull FeatherChatAccessible plugin, @NotNull Map<String, Object> values) throws IllegalArgumentException {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        String[] parts = ((String)values.get("key")).split(":");
        NamespacedChannelKey key = new NamespacedChannelKey(parts[0], parts[1]);

        UUID uuid = UUID.fromString((String)values.get("uuid"));
        String name = (String)values.get("name");

        String strDisplayName = (String)values.get("displayName");
        Component displayName = strDisplayName == null ? null : miniMessage.deserialize(strDisplayName);

        UUID owner = UUID.fromString((String)values.get("owner"));
        String messageFormat = (String)values.get("messageFormat");

        UserChatChannel channel = new UserChatChannelImpl(plugin, uuid, key, owner, name);
        channel.setDisplayName(displayName);
        channel.setMessageFormat(messageFormat);
        return channel;
    }

    @NotNull
    private File ymlFile(@NotNull ChatChannel channel) {
        return new File(channelsDir, channel.getUUID() + ".yml");
    }

    @NotNull
    private File ymlFile(@NotNull UUID channelUUID) {
        return new File(channelsDir, channelUUID + ".yml");
    }

}
