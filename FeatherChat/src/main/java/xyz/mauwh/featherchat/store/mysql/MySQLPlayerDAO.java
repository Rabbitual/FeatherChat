package xyz.mauwh.featherchat.store.mysql;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.messenger.ChatMessengerFactory;

import java.sql.*;
import java.util.UUID;

public final class MySQLPlayerDAO {

    private final MySQLDatabase database;
    private final ChatMessengerFactory<?> messengerFactory;

    public MySQLPlayerDAO(@NotNull MySQLDatabase database, @NotNull ChatMessengerFactory<?> messengerFactory) {
        this.database = database;
        this.messengerFactory = messengerFactory;
    }

    public void create(@NotNull Player player) throws SQLException {
        Connection connection = database.getValidConnection();
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `players` (uuid, name, display_name) VALUES (?, ?, ?)")) {
            statement.setString(1, player.getUUID().toString().replace("-", ""));
            statement.setString(2, player.getName());
            if (player.hasDisplayName()) {
                statement.setString(3, MiniMessage.miniMessage().serialize(player.getDisplayName()));
            } else {
                statement.setNull(3, Types.VARCHAR);
            }
            statement.executeUpdate();
        }
    }

    public Player read(@NotNull UUID uuid) throws SQLException {
        Connection connection = database.getValidConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `players` WHERE uuid=?")) {
            statement.setString(1, uuid.toString().replace("-", ""));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalArgumentException("No player found with uuid " + uuid);
                }
                String name = resultSet.getString(2);
                String displayName = resultSet.getString(3);
                Player player = messengerFactory.player(uuid, name);
                if (displayName != null) {
                    player.setDisplayName(MiniMessage.miniMessage().deserialize(displayName));
                }
                return player;
            }
        }
    }

    public void update(@NotNull Player player) throws SQLException {
        Connection connection = database.getValidConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `players` SET name=?, display_name=?")) {
            statement.setString(1, player.getName());
            if (player.hasDisplayName()) {
                statement.setString(2, MiniMessage.miniMessage().serialize(player.getDisplayName()));
            } else {
                statement.setNull(2, Types.VARCHAR);
            }
            statement.executeUpdate();
        }
    }

    public void delete(@NotNull Player player) throws SQLException {
        Connection connection = database.getValidConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `player` WHERE uuid='?'")) {
            statement.setString(1, player.getUUID().toString().replace("-", ""));
        }
    }

}
