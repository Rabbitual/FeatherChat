package xyz.mauwh.featherchat.api.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ChatChannels {
    @NotNull ChatChannel getDebugChannel();
    @NotNull ChatChannel createChannel(@NotNull Player<?> owner, @NotNull String name);
    void updateChannel(@NotNull UserChatChannel channel);
    void dissolveChannel(@NotNull UserChatChannel channel);
    @Nullable UserChatChannel resolveByUUID(@NotNull UUID uuid);
    @NotNull Map<UUID, UserChatChannel> resolveByUUIDs(@NotNull Iterable<UUID> uuids);
    @Nullable UserChatChannel resolveByKey(@NotNull NamespacedChannelKey key);
    @NotNull Set<UserChatChannel> getCachedUserChannels();
    @NotNull Set<UserChatChannel> filterByName(@NotNull String name);
    @NotNull Set<UserChatChannel> filterByName(@NotNull String name, @NotNull Set<UserChatChannel> channels);
    @NotNull Set<UserChatChannel> filterByOwner(@NotNull Player<?> owner);
    @NotNull Set<UserChatChannel> filterByOwner(@NotNull UUID owner);
    @NotNull Set<UserChatChannel> filterByParticipant(@NotNull Player<?> participant);
    @NotNull Set<UserChatChannel> filterByParticipant(@NotNull UUID participant);
}
