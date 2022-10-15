package xyz.mauwh.featherchat.store.mysql;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.store.DataAccessObject;

import java.sql.*;
import java.util.UUID;

public class MySQLChannelMemberDAO implements DataAccessObject<ChannelMember, UUID> {

    private final MySQLDatabase database;

    public MySQLChannelMemberDAO(@NotNull MySQLDatabase database) {
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

    /**
     * @inheritDoc
     * @deprecated uuid is ambiguous and cannot be used to identify an individual channel member
     * @see #read(java.util.UUID, java.util.UUID)
     */
    @Override
    @NotNull
    @Deprecated
    public ChannelMember read(@NotNull UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not yet implemented - please use MySQLChannelMemberDAO#read(UUID, UUID)");
    }

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

    @NotNull
    private String nonHyphenated(@NotNull UUID uuid) {
        return uuid.toString().replace("-", "");
    }

}
