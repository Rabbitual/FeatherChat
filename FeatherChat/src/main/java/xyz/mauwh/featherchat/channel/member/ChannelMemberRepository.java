package xyz.mauwh.featherchat.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.api.channel.member.ChannelMembers;

import java.util.*;
import java.util.function.BiConsumer;

public final class ChannelMemberRepository implements ChannelMembers.Global {

    final Set<ChannelMember> members;
    private final Map<UUID, ChannelMembersMap> channelViews;
    private final Map<UUID, ChannelMembersMap> playerViews;

    public ChannelMemberRepository() {
        this.members = new HashSet<>();
        this.channelViews = new HashMap<>();
        this.playerViews = new HashMap<>();
    }

    @Override
    @NotNull
    public ChannelMembers.Global global() {
        return this;
    }

    @Override
    @NotNull
    public ChannelMember create(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        ChannelMember member = new ChannelMember(channelUUID, playerUUID);
        channelViews.computeIfAbsent(channelUUID, uuid -> new ChannelMembersMap(this)).put(playerUUID, member);
        playerViews.computeIfAbsent(playerUUID, uuid -> new ChannelMembersMap(this)).put(channelUUID, member);
        return member;
    }

    @Override
    @Nullable
    public ChannelMember get(@NotNull UUID channelUUID, @NotNull UUID playerUUID) {
        ChannelMembersMap channelView = channelViews.get(channelUUID);
        return channelView == null ? null : channelView.get(playerUUID);
    }

    @Override
    @NotNull
    public Collection<ChannelMember> getAllByChannel(@NotNull UUID channelUUID) {
        ChannelMembersMap channelView = channelViews.get(channelUUID);
        return channelView == null ? Collections.emptySet() : channelView.getAll();
    }

    @Override
    @NotNull
    public Collection<ChannelMember> getAllByPlayer(@NotNull UUID playerUUID) {
        ChannelMembersMap playerView = playerViews.get(playerUUID);
        return playerView == null ? Collections.emptySet() : playerView.getAll();
    }

    @Override
    public void remove(@NotNull ChannelMember member) {
        members.remove(member);
        ChannelMembersMap view = channelViews.get(member.getChannelUUID());
        if (view != null) {
            view.members.remove(member.getPlayerUUID(), member);
        }
        view = playerViews.get(member.getPlayerUUID());
        if (view != null) {
            view.members.remove(member.getPlayerUUID(), member);
        }
    }

    @Override
    public void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @Nullable BiConsumer<ChannelMember, Throwable> callback) {

    }

    @Override
    public void loadByChannel(@NotNull UUID channelUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback) {

    }

    @Override
    public void loadByPlayer(@NotNull UUID playerUUID, @Nullable BiConsumer<Set<UserChatChannel>, Throwable> callback) {

    }

    @NotNull
    @Override
    public Iterator<ChannelMember> iterator() {
        return members.iterator();
    }

}
