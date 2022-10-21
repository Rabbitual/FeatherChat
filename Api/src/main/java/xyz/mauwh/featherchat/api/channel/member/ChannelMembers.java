package xyz.mauwh.featherchat.api.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface ChannelMembers extends Iterable<ChannelMember> {

    @NotNull ChannelMembers.Global global();

    interface View extends ChannelMembers {
        void put(@NotNull UUID uuid, @NotNull ChannelMember member);

        @Nullable ChannelMember get(@NotNull UUID playerUUID);
    }

    interface Global extends ChannelMembers {
        @NotNull ChannelMember create(@NotNull UUID channelUUID, @NotNull UUID playerUUID);

        @Nullable ChannelMember get(@NotNull UUID channelUUID, @NotNull UUID playerUUID);

        @NotNull Collection<ChannelMember> getAllByChannel(@NotNull UUID channelUUID);

        @NotNull Collection<ChannelMember> getAllByPlayer(@NotNull UUID playerUUID);

        void remove(@NotNull ChannelMember member);

        void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @Nullable BiConsumer<ChannelMember, Throwable> callback);

        void loadByChannel(@NotNull UUID channelUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback);

        void loadByPlayer(@NotNull UUID playerUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback);
    }
}
