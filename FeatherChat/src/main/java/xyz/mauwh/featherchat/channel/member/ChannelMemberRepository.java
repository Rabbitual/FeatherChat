package xyz.mauwh.featherchat.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.api.channel.member.ChannelMembers;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class ChannelMemberRepository implements ChannelMembers {

    private final Map<UUID, Set<ChannelMember>> byChannel;
    private final Map<UUID, Set<ChannelMember>> byPlayer;

    public ChannelMemberRepository() {
        this.byChannel = new HashMap<>();
        this.byPlayer = new HashMap<>();
    }

    @Override
    @NotNull
    public ChannelMember create(@NotNull UserChatChannel channel, @NotNull Player player) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @Nullable
    public ChannelMember get(@NotNull UserChatChannel channel, @NotNull Player player) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @Nullable
    public ChannelMember get(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @NotNull
    public Set<ChannelMember> getByChannel(@NotNull UserChatChannel channel) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @NotNull
    public Set<ChannelMember> getByPlayer(@NotNull Player player) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @Nullable BiConsumer<ChannelMember, Throwable> callback) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void loadByChannel(@NotNull UUID channelUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void loadByPlayer(@NotNull UUID playerUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
