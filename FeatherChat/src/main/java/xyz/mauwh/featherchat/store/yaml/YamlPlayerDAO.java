package xyz.mauwh.featherchat.store.yaml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.messenger.ChatMessengerFactory;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.messenger.PlayerAccessible;
import xyz.mauwh.featherchat.store.PlayerDAO;
import xyz.mauwh.featherchat.store.yaml.serializer.GenericYamlSerializer;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public final class YamlPlayerDAO implements PlayerDAO {

    private static final Logger logger = Logger.getLogger("FeatherChat");
    private final ChatMessengerFactory<?> messengerFactory;
    private final File playersDir;

    public YamlPlayerDAO(@NotNull File dataFolder, @NotNull ChatMessengerFactory<?> messengerFactory) {
        this.messengerFactory = messengerFactory;
        this.playersDir = new File(dataFolder, "players");
    }

    @Override
    public void create(@NotNull Player player) throws DataEntityAccessException {
        playersDir.mkdirs();
        File file = ymlFile(player.getUUID());
        try {
            if (file.createNewFile()) {
                this.update(player);
                return;
            }
            logger.warning("Attempted to create player data where player data already exists");
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to save player data", err);
        }
    }

    @Override
    public Player read(@NotNull UUID playerUUID) throws DataEntityAccessException {
        File file = ymlFile(playerUUID);
        if (!file.exists()) {
            throw new DataEntityAccessException("Player data does not exist");
        }
        Yaml yaml = new Yaml();
        try (InputStream input = new FileInputStream(file)) {
            return deserialize(yaml.load(input));
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to read player data", err);
        }
    }

    @Override
    public void update(@NotNull Player player) throws DataEntityAccessException {
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
    public void delete(@NotNull Player player) {
        ymlFile(player).delete();
    }

    @NotNull
    public Map<String, Object> serialize(@NotNull Player player) {
        Map<String, Object> values = new HashMap<>();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        values.put("uuid", player.getUUID().toString());
        values.put("name", player.getName());
        values.put("channels", player.getChannels().stream().map(UUID::toString).toList());
        if (player.hasDisplayName()) {
            values.put("displayName", miniMessage.serialize(player.getDisplayName()));
        }
        return values;
    }

    @NotNull
    public Player deserialize(@NotNull Map<String, Object> values) {
        GenericYamlSerializer serializer = new GenericYamlSerializer();
        UUID uuid = serializer.deserializeUUID(values, "uuid");
        String name = (String)values.get("name");

        Player player = messengerFactory.player(uuid, name);
        Component displayName = serializer.deserializeComponent(values, "display-name", GenericYamlSerializer.COLORED);
        if (displayName != null) {
            player.setDisplayName(displayName);
        }

        List<UUID> channelUUIDs = serializer.deserializeUUIDList(values, "channels");
        ((PlayerAccessible)player).setChannels(channelUUIDs);
        return player;
    }

    @NotNull
    private File ymlFile(@NotNull Player player) {
        return ymlFile(player.getUUID());
    }

    @NotNull
    private File ymlFile(@NotNull UUID playerUUID) {
        return new File(playersDir, playerUUID + ".yml");
    }

}
