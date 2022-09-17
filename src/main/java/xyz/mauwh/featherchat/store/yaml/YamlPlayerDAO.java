package xyz.mauwh.featherchat.store.yaml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.messenger.ChatMessengerPlayerImpl;
import xyz.mauwh.featherchat.messenger.Player;
import xyz.mauwh.featherchat.store.DataAccessObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class YamlPlayerDAO implements DataAccessObject<Player, UUID> {

    private final FeatherChat plugin;
    private final File playersDir;

    public YamlPlayerDAO(@NotNull FeatherChat plugin) {
        this.plugin = plugin;
        this.playersDir = new File(plugin.getDataFolder(), "players");
    }

    @Override
    public void create(@NotNull Player player) throws DataEntityAccessException {
        playersDir.mkdirs();
        File file = ymlFile(player);
        try {
            if (file.createNewFile()) {
                this.update(player);
            }
        } catch (IOException err) {
            throw new DataEntityAccessException("An error occurred while attempting to save player", err);
        }
    }

    @Override
    public Player read(@NotNull UUID playerUUID) throws DataEntityAccessException {
        File file = ymlFile(playerUUID);
        if (!file.exists()) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) {
                throw new DataEntityAccessException("Unable to read player data from UUID '" + playerUUID + "': player does not exist");
            }
            Player created = new ChatMessengerPlayerImpl(plugin, player);
            create(created);
            return created;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return deserialize(plugin, config.getValues(true));
    }

    @Override
    public void update(@NotNull Player player) throws DataEntityAccessException {
        File file = new File(playersDir, player.getUUID() + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Attempted to update non-existent player data file ('" + player.getUUID() + ".yml')");
            return;
        }
        FileConfiguration config = new YamlConfiguration();
        serialize(player).forEach(config::set);
        try {
            config.save(file);
        } catch (IOException err) {
            throw new DataEntityAccessException("Unexpected exception occurred while attempting to save updated config", err);
        }
    }

    @Override
    public void delete(@NotNull Player player) {
        ymlFile(player).delete();
    }

    @NotNull
    public static Map<String, Object> serialize(@NotNull Player player) {
        Map<String, Object> values = new HashMap<>();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        values.put("uuid", player.getUUID().toString());
        values.put("name", player.getName());
        values.put("channels", player.getChannels().stream().map(UUID::toString).toList());
        player.getDisplayName().ifPresent(displayName -> values.put("displayName", miniMessage.serialize(displayName)));
        return values;
    }

    @NotNull
    public static Player deserialize(@NotNull FeatherChat plugin, @NotNull Map<String, Object> values) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        UUID uuid = UUID.fromString(values.get("uuid").toString());
        String name = values.get("name").toString();
        ChatMessengerPlayerImpl player = new ChatMessengerPlayerImpl(plugin, uuid, name);

        if (values.containsKey("displayName")) {
            Component displayName = miniMessage.deserialize(values.get("displayName").toString());
            player.setDisplayName(displayName, false);
        }

        Set<UUID> channelUUIDs = new HashSet<>();
        List<?> strUUIDs = (List<?>)values.get("channels");
        strUUIDs.forEach(obj -> {
            try {
                channelUUIDs.add(UUID.fromString(obj.toString()));
            } catch (IllegalArgumentException err) {
                plugin.getLogger().warning("Encountered a malformed channel UUID while reading player data file (file: '" + uuid + ".yml', channel UUID: '" + obj + "')");
            }
        });
        player.setChannels(channelUUIDs);
        return player;
    }

    @NotNull
    private File ymlFile(@NotNull Player player) {
        return new File(playersDir, player.getUUID() + ".yml");
    }

    @NotNull
    private File ymlFile(@NotNull UUID playerUUID) {
        return new File(playersDir, playerUUID + ".yml");
    }

}
