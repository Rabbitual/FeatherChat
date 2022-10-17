package xyz.mauwh.featherchat.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;

public interface PlayerDAO {
    void create(@NotNull Player player);
    @Nullable Player read(@NotNull UUID playerUUID);
    void update(@NotNull Player player);
    void delete(@NotNull Player player);
}
