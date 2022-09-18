package xyz.mauwh.featherchat.api.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ChatChannel {

    @NotNull NamespacedChannelKey getKey();

    @NotNull UUID getUUID();

    @NotNull String getName();

    @NotNull Optional<Component> getDisplayName();

    @NotNull Component getFriendlyName();

    void setDisplayName(@Nullable Component displayName);

    @NotNull String getMessageFormat();

    void setMessageFormat(@NotNull String format);

    @NotNull Set<UUID> getMembers();

    boolean isMember(@NotNull UUID member);

    boolean isMember(@NotNull ChatMessenger<?> member);

    void sendMessage(@NotNull ChatMessenger<?> sender, @NotNull Component component);
}
