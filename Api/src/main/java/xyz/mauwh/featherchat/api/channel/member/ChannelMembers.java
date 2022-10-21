package xyz.mauwh.featherchat.api.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

        void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @NotNull BiConsumer<ChannelMember, Throwable> callback);

        void loadByChannel(@NotNull UUID channelUUID, @NotNull BiConsumer<Collection<ChannelMember>, Throwable> callback);

        void loadByPlayer(@NotNull UUID playerUUID, @NotNull BiConsumer<Collection<ChannelMember>, Throwable> callback);
    }
}
