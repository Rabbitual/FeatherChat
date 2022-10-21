package xyz.mauwh.featherchat.store.mysql;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.store.ChannelMemberDAO;
import xyz.mauwh.featherchat.store.yaml.serializer.GenericYamlSerializer;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

public class MySQLChannelMemberDAO implements ChannelMemberDAO {

    private final Logger logger;
    private final MySQLDatabase database;

    public MySQLChannelMemberDAO(@NotNull MySQLDatabase database) {
        this.logger = Logger.getLogger("FeatherChat");
        this.database = database;
    }

    @Override
    public void create(@NotNull ChannelMember member) throws DataEntityAccessException {
        try {
            Connection connection = database.getValidConnection();
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `channel_members` (channel_uuid, player_uuid) VALUES (?, ?)")) {
                statement.setString(1, member.getChannelUUID().toString().replace("-", ""));
                statement.setString(2, member.getPlayerUUID().toString().replace("-", ""));
                statement.executeUpdate();
            }
        } catch (SQLException err) {
            throw new DataEntityAccessException("Unable to create player data", err);
        }
    }

    @Override
    @NotNull
    public ChannelMember read(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        try {
            Connection connection = database.getValidConnection();
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `channel_members` WHERE channel_uuid=?,player_uuid=?")) {
                statement.setString(1, nonHyphenated(channelUUID));
                statement.setString(2, nonHyphenated(playerUUID));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new DataEntityAccessException("No channel member found");
                    }
                    return new ChannelMember(channelUUID, playerUUID);
                }
            }
        } catch (SQLException err) {
            throw new DataEntityAccessException("Unable to read player data", err);
        }
    }

    @Override
    public void update(@NotNull ChannelMember member) throws DataEntityAccessException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void delete(@NotNull ChannelMember member) throws DataEntityAccessException {
        try {
            Connection connection = database.getValidConnection();
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `channel_members` WHERE channel_uuid=?,player_uuid=?")) {
                statement.setString(1, nonHyphenated(member.getChannelUUID()));
            }
        } catch (SQLException err) {
            throw new DataEntityAccessException("Unable to delete player data", err);
        }
    }

    @Override
    @NotNull
    public Collection<ChannelMember> findAllByChannelUUID(@NotNull UUID channelUUID) {
        try {
            Connection connection = database.getValidConnection();
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT channel_members.*, players.name, players.display_name
                    FROM channel_members
                    INNER JOIN players
                    ON channel_members.player_uuid = players.uuid
                    WHERE channel_members.channel_uuid = ?""")) {
                statement.setString(1, channelUUID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    return readAllByUUID(resultSet, "player_uuid", playerUUID -> new ChannelMember(channelUUID, playerUUID));
                }
            }
        } catch (SQLException err) {
            throw new DataEntityAccessException("Unable to read player data");
        }
    }

    @Override
    @NotNull
    public Collection<ChannelMember> findAllByPlayerUUID(@NotNull UUID playerUUID) {
        try {
            Connection connection = database.getValidConnection();
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT channel_members.*, players.name, players.display_name
                    FROM channel_members
                    INNER JOIN players
                    ON channel_members.player_uuid = players.uuid
                    WHERE channel_members.player_uuid = ?""")) {
                statement.setString(1, playerUUID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    return readAllByUUID(resultSet, "channel_uuid", channelUUID -> new ChannelMember(channelUUID, playerUUID));
                }
            }
        } catch (SQLException err) {
            throw new DataEntityAccessException("Unable to read player data");
        }
    }

    @NotNull
    private Collection<ChannelMember> readAllByUUID(@NotNull ResultSet resultSet, @NotNull String column, @NotNull Function<UUID, ChannelMember> function) {
        Collection<ChannelMember> members = new HashSet<>();
        try {
            while (resultSet.next()) {
                try {
                    String uuid = resultSet.getString(column);
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    ChannelMember member = function.apply(UUID.fromString(uuid));
                    member.setName(name);
                    if (displayName != null) {
                        Component component = GenericYamlSerializer.COLORED.deserialize(displayName);
                        member.setDisplayName(component);
                    }
                    members.add(member);
                } catch (SQLException err) {
                    logger.warning(String.format("Unable to read player data: %s - skipping...", err.getMessage()));
                }
            }
        } catch (SQLException err) {
            logger.severe("Unexpected SQL result set cursor error: " + err.getMessage());
        }
        return members;
    }

    @NotNull
    private String nonHyphenated(@NotNull UUID uuid) {
        return uuid.toString().replace("-", "");
    }

}
