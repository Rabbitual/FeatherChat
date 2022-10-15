package xyz.mauwh.featherchat.api.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface ChannelMembers {

    @NotNull ChannelMember create(@NotNull UserChatChannel channel, @NotNull Player player);

    @Nullable ChannelMember get(@NotNull UserChatChannel channel, @NotNull Player player);

    @Nullable ChannelMember get(@NotNull UUID channelUUID, @NotNull UUID playerUUID);

    @NotNull Set<ChannelMember> getByChannel(@NotNull UserChatChannel channel);

    @NotNull Set<ChannelMember> getByPlayer(@NotNull Player player);

    void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @Nullable BiConsumer<ChannelMember, Throwable> callback);

    void loadByChannel(@NotNull UUID channelUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback);

    void loadByPlayer(@NotNull UUID playerUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback);
}
