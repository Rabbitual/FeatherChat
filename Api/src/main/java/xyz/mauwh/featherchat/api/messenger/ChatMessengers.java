package xyz.mauwh.featherchat.api.messenger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface ChatMessengers<T> {
    /**
     * Gets the sole console messenger of this ChatMessengers instance
     * @return The console messenger
     */
    @NotNull ChatMessenger getConsole();

    /**
     * Gets a chat messenger by its platform implementation
     * @param sender - The platform implementation to get a chat messenger by
     * @return A {@link ChatMessenger} object representing the provided sender
     */
    @Nullable ChatMessenger getBySender(@NotNull T sender);

    /**
     * Gets a chat messenger by its name
     * @param name - The chat messenger name
     * @return The chat messenger with the provided name, or null if the name could not be found
     */
    @Nullable Player getByName(@NotNull String name);

    /**
     * Gets a chat messenger by its UUID
     * @param uuid - The chat messenger UUID
     * @return The chat messenger with the provided UUID, or null if the UUID could not be found
     */
    @Nullable Player getByUUID(@NotNull UUID uuid);

    void loadPlayer(@NotNull UUID uuid, @Nullable String name, BiConsumer<Player, Throwable> callback, boolean async);

    /**
     * Updates the provided player in persistent storage.
     * @param player - The player to be updated
     */
    void update(@NotNull Player player);

    void updateAndRemove(@NotNull Player player);

    @NotNull Collection<Player> getAll();
}
