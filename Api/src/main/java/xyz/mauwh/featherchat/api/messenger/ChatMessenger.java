package xyz.mauwh.featherchat.api.messenger;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ChatMessenger extends Identified {
    /**
     * @return This chat messenger's name
     */
    @NotNull String getName();

    /**
     * @return This chat messenger's display name
     */
    @NotNull Optional<Component> getDisplayName();

    /**
     * Gets the "friendly name" of this chat messenger, which will always be displayed to other chat messengers. As a
     * display name may not always be set by the user, the "friendly name" is the display name if set, else it will
     * return {@link #getName()}.
     * @return The "friendly name" of this chat messenger
     */
    @NotNull Component getFriendlyName();

    /**
     * Sets the display name of this chat messenger
     * @param displayName - The new display name
     */
    void setDisplayName(@Nullable Component displayName);

    /**
     * Gets the server platform's native implementation of this chat messenger.
     * @param <T> Platform implementation type
     * @return This chat messenger's handle
     */
    @Nullable <T> T getHandle();

    /**
     * @return true if this chat messenger represents a player
     */
    boolean isPlayer();

    /**
     * Sends a message to this chat messenger
     * @param sender - The chat messenger sending this message
     * @param message - The message to be sent
     */
    void sendMessage(@Nullable ChatMessenger sender, @NotNull Component message);

    /**
     * Sends a message to this chat messenger
     * @param message - The message to be sent
     */
    void sendMessage(@NotNull Component message);

    /**
     * Checks the provided permission node for this chat messenger.
     * @param permission - The permission name
     * @return true if this chat messenger has permission
     */
    boolean hasPermission(String permission);
}
