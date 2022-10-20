package xyz.mauwh.featherchat.api.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ChatChannel {
    /**
     * Gets the {@link NamespacedChannelKey} of this chat channel.
     * @return The channel key of this chat channel
     */
    @NotNull NamespacedChannelKey getKey();

    /**
     * Gets the UUID of this chat channel.
     * @return The UUID of this chat channel
     */
    @NotNull UUID getUUID();

    /**
     * Gets the name of this chat channel.
     * @return This name of this chat channel
     */
    @NotNull String getName();

    boolean hasDisplayName();

    /**
     * Gets the display name of this chat channel, which will always be displayed in chat. If no display name is set,
     * else it will return the result of {@link #getName()} as a component.
     * @return The "friendly name" of this chat channel
     */
    @NotNull Component getDisplayName();

    /**
     * Sets the display name of this chat messenger.
     * @param displayName - The new display name
     */
    void setDisplayName(@Nullable Component displayName);

    /**
     * Gets the message format used by this chat channel.
     * @return The message format of this chat channel
     */
    @NotNull String getMessageFormat();

    /**
     * Sets the message format used by this chat channel.
     * @param format - The new message format
     */
    void setMessageFormat(@NotNull String format);

    /**
     * Gets a set of UUIDs of players belonging to this channel
     * @return a set of UUIDs of this channel's member
     */
    @NotNull Set<UUID> getMembers();

    /**
     * Checks if the provided UUID belongs to a member.
     * @param member - The supposed channel member's UUID
     * @return true if the provided UUID belongs to a member
     */
    boolean isMember(@NotNull UUID member);

    /**
     * Checks if the provided player is a channel member.
     * @param member - The supposed channel member
     * @return true if the provided player is a channel member
     */
    boolean isMember(@NotNull Player member);

    /**
     * Adds the provided player to this channel's members.
     * @param member - The new channel member
     * @return true if the player was successfully added as a member
     */
    boolean addMember(@NotNull Player member);

    /**
     * Removes the provided player from this channel's members.
     * @param member - The channel member
     * @return true if the player was successfully removed as a member
     */
    boolean removeMember(@NotNull Player member);

    /**
     * Sends a message to all online members in this channel.
     * @param component - The component message to be sent
     */
    void sendMessage(@NotNull Component component);

    /**
     * Sends a message to all online members in this channel.
     * @param sender - The sender of this message
     * @param component - The component message to be sent
     */
    void sendMessage(@NotNull ChatMessenger sender, @NotNull Component component);
}
