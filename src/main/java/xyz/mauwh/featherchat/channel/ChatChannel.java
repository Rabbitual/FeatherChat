package xyz.mauwh.featherchat.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.NamespacedChannelKey;
import xyz.mauwh.featherchat.messenger.ChatMessenger;

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

    void sendMessage(@NotNull ChatMessenger sender, @NotNull Component component);
}
