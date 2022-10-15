package xyz.mauwh.featherchat.store.mysql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MySQLDatabase {

    private final String address;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    public MySQLDatabase(@NotNull String address, @NotNull String database,
                         @NotNull String username, @NotNull String password) {
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @NotNull
    public Connection getValidConnection() throws SQLException {
        if (isConnectionOpen()) {
            return connection;
        }
        return connection = DriverManager.getConnection("jdbc:mysql://" + address + "/" + database, username, password);
    }

    public boolean isConnectionOpen() throws SQLException {
        return connection != null && connection.isValid(30);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        Connection connection = getValidConnection();
        connection.setAutoCommit(false);

        try (PreparedStatement createPlayers = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS players(
                    uuid VARCHAR(32) PRIMARY KEY,
                    name VARCHAR(16) not null,
                    display_name VARCHAR(32)
                )""")) {
            createPlayers.executeUpdate();
        }

        try (PreparedStatement createChannels = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS channels(
                    uuid VARCHAR(32) PRIMARY KEY,
                    name VARCHAR(16) not null,
                    display_name VARCHAR(32)
                )""")) {
            createChannels.executeUpdate();
        }

        try (PreparedStatement createChannelMembers = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS channel_members(
                    channel_uuid VARCHAR(32),
                    player_uuid VARCHAR(32),
                    PRIMARY KEY(channel_uuid, player_uuid)
                )""")) {
            createChannelMembers.executeUpdate();
        }

        connection.commit();
        connection.setAutoCommit(true);
    }

}
