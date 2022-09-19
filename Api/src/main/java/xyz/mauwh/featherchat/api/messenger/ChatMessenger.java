package xyz.mauwh.featherchat.api.messenger;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ChatMessenger<T> extends Identified {
    @NotNull String getName();
    @NotNull Optional<Component> getDisplayName();
    @NotNull Component getFriendlyName();
    void setDisplayName(@Nullable Component displayName);
    @Nullable T getHandle();
    boolean isPlayer();
    void sendMessage(@Nullable ChatMessenger<?> sender, @NotNull Component message);
    void sendMessage(@NotNull Component message);
    boolean hasPermission(String permission);
}
