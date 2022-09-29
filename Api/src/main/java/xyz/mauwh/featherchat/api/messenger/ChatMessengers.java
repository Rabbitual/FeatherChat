package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ChatMessengers<T, U extends ChatMessenger, V extends Player> {
    /**
     * Gets the sole console messenger of this ChatMessengers instance
     * @return The console messenger
     */
    @NotNull U getConsole();

    /**
     * Gets a chat messenger by its platform implementation
     * @param sender - The platform implementation to get a chat messenger by
     * @return A {@link ChatMessenger} object representing the provided sender
     */
    @NotNull U getBySender(@NotNull T sender);

    /**
     * Gets a chat messenger by its name
     * @param name - The chat messenger name
     * @return The chat messenger with the provided name, or null if the name could not be found
     */
    @Nullable V getByName(@NotNull String name);

    /**
     * Gets a chat messenger by its UUID
     * @param uuid - The chat messenger UUID
     * @return The chat messenger with the provided UUID, or null if the UUID could not be found
     */
    @NotNull V getByUUID(@NotNull UUID uuid);

    /**
     * Updates the provided player in persistent storage.
     * @param player - The player to be updated
     */
    void update(@NotNull V player);
    void updateAndRemove(@NotNull V player);
}
