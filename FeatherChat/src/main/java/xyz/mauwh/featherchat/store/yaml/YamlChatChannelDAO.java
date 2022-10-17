package xyz.mauwh.featherchat.store.yaml;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.channel.UserChatChannelImpl;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;
import xyz.mauwh.featherchat.store.ChatChannelDAO;
import xyz.mauwh.featherchat.store.yaml.serializer.GenericYamlSerializer;

import java.io.*;
import java.util.*;

public final class YamlChatChannelDAO implements ChatChannelDAO {

    private final FeatherChatPlugin plugin;
    private final File channelsDir;

    public YamlChatChannelDAO(@NotNull FeatherChatPlugin plugin) {
        this.plugin = plugin;
        this.channelsDir = new File(plugin.getDataFolder(), "channels");
    }

    @Override
    public void create(@NotNull UserChatChannel channel) throws DataEntityAccessException {
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
        if (!ymlFile(channel).delete()) {
            plugin.getLogger().warning("Attempted to delete chat channel which does not exist");
        }
    }

    @NotNull
    public static Map<String, Object> serialize(@NotNull UserChatChannel channel) {
        Map<String, Object> values = new HashMap<>();
        values.put("key", channel.getKey().toString());
        values.put("uuid", channel.getUUID().toString());
        values.put("name", channel.getName());

        channel.getDisplayName().ifPresent(displayName -> values.put("display-name", GenericYamlSerializer.COLORED.serialize(displayName)));
        values.put("owner", channel.getOwner().toString());
        values.put("members", channel.getMembers().stream().map(String::valueOf).toList());
        values.put("message-format", channel.getMessageFormat());
        return values;
    }

    @NotNull
    public static UserChatChannel deserialize(@NotNull FeatherChatPlugin plugin, @NotNull Map<String, Object> values) throws IllegalArgumentException {
        GenericYamlSerializer serializer = new GenericYamlSerializer();
        serializer.checkRequiredChannelValuesNonNull(values);

        NamespacedChannelKey key = serializer.deserializeChannelKey(values, "key");
        UUID uuid = serializer.deserializeUUID(values, "uuid");
        String name = (String)values.get("name");
        Component displayName = serializer.deserializeComponent(values, "display-name", GenericYamlSerializer.COLORED_DECORATED);
        UUID owner = serializer.deserializeUUID(values, "owner");
        List<UUID> members = serializer.deserializeUUIDList(values, "members");
        String messageFormat = (String)values.get("message-format");

        UserChatChannelImpl channel = new UserChatChannelImpl(plugin, uuid, key, owner, name);
        channel.setMembers(members);
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
