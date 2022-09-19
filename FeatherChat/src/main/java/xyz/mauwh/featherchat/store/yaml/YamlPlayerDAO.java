package xyz.mauwh.featherchat.store.yaml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengerFactory;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.messenger.PlayerAccessible;
import xyz.mauwh.featherchat.store.DataAccessObject;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public final class YamlPlayerDAO<T, U extends ChatMessenger<T>, V extends Player<T>> implements DataAccessObject<V, UUID> {

    private static final Logger logger = Logger.getLogger("FeatherChatData");
    private final ChatMessengerFactory<T, U, V> messengerFactory;
    private final File playersDir;

    public YamlPlayerDAO(@NotNull File dataFolder, @NotNull ChatMessengerFactory<T, U, V> messengerFactory) {
        this.messengerFactory = messengerFactory;
        this.playersDir = new File(dataFolder, "players");
    }

    @Override
    public void create(@NotNull V player) throws DataEntityAccessException {
        if (!player.isPlayer()) {
            throw new DataEntityAccessException("Unable to save data of non-player messenger");
        }
        playersDir.mkdirs();
        File file = ymlFile(player);
        try {
            if (file.createNewFile()) {
                this.update(player);
            }
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to save player data", err);
        }
    }

    @Override
    public V read(@NotNull UUID playerUUID) throws DataEntityAccessException {
        File file = ymlFile(playerUUID);
        if (!file.exists()) {
            V created = messengerFactory.player(playerUUID);
            create(created);
            return created;
        }
        Yaml yaml = new Yaml();
        try (InputStream input = new FileInputStream(file)) {
            return deserialize(yaml.load(input));
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to read player data", err);
        }
    }

    @Override
    public void update(@NotNull V player) throws DataEntityAccessException {
        File file = new File(playersDir, player.getUUID() + ".yml");
        if (!file.exists()) {
            logger.warning("Attempted to update non-existent player data file ('" + player.getUUID() + ".yml')");
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            new Yaml().dump(serialize(player), writer);
        } catch (IOException err) {
            throw new DataEntityAccessException("Unexpected exception occurred while attempting to save updated config", err);
        }
    }

    @Override
    public void delete(@NotNull V player) {
        ymlFile(player).delete();
    }

    @NotNull
    public Map<String, Object> serialize(@NotNull V player) {
        Map<String, Object> values = new HashMap<>();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        values.put("uuid", player.getUUID().toString());
        values.put("name", player.getName());
        values.put("channels", player.getChannels().stream().map(UUID::toString).toList());
        player.getDisplayName().ifPresent(displayName -> values.put("displayName", miniMessage.serialize(displayName)));
        return values;
    }

    @NotNull
    public V deserialize(@NotNull Map<String, Object> values) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        UUID uuid = UUID.fromString(values.get("uuid").toString());
        String name = values.get("name").toString();
        V player = messengerFactory.offlinePlayer(uuid, name);

        if (values.containsKey("displayName")) {
            Component displayName = miniMessage.deserialize(values.get("displayName").toString());
            ((PlayerAccessible<?>)player).setDisplayName(displayName, false);
        }

        Set<UUID> channelUUIDs = new HashSet<>();
        List<?> strUUIDs = (List<?>)values.get("channels");
        strUUIDs.forEach(obj -> {
            try {
                channelUUIDs.add(UUID.fromString(obj.toString()));
            } catch (IllegalArgumentException err) {
                logger.warning("Encountered a malformed channel UUID while reading player data file (file: '" + uuid + ".yml', channel UUID: '" + obj + "')");
            }
        });

        ((PlayerAccessible<?>)player).setChannels(channelUUIDs);
        return player;
    }

    @NotNull
    private File ymlFile(@NotNull V player) {
        if (!player.isPlayer()) {
            throw new IllegalArgumentException("Cannot create yml file for non-player messenger");
        }
        return new File(playersDir, (player).getUUID() + ".yml");
    }

    @NotNull
    private File ymlFile(@NotNull UUID playerUUID) {
        return new File(playersDir, playerUUID + ".yml");
    }

}
