package xyz.mauwh.featherchat.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.api.channel.member.ChannelMembers;
import xyz.mauwh.featherchat.store.ChannelMemberDAO;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ChannelMemberRepository implements ChannelMembers.Global {

    final Set<ChannelMember> members;
    private final Map<UUID, ChannelMembersMap> channelViews;
    private final Map<UUID, ChannelMembersMap> playerViews;
    private final ChannelMemberDAO channelMemberDAO;

    public ChannelMemberRepository(@NotNull ChannelMemberDAO channelMemberDAO) {
        this.members = new HashSet<>();
        this.channelViews = new HashMap<>();
        this.playerViews = new HashMap<>();
        this.channelMemberDAO = channelMemberDAO;
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
        CompletableFuture.runAsync(() -> channelMemberDAO.create(member));
        cacheMember(member);
        return member;
    }

    private void cacheMember(@NotNull ChannelMember member) {
        UUID channelUUID = member.getChannelUUID();
        UUID playerUUID = member.getPlayerUUID();
        members.add(member);
        channelViews.computeIfAbsent(channelUUID, uuid -> new ChannelMembersMap(this)).put(playerUUID, member);
        playerViews.computeIfAbsent(playerUUID, uuid -> new ChannelMembersMap(this)).put(channelUUID, member);
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
    }

    @Override
    public void load(@NotNull UUID channelUUID, @NotNull UUID playerUUID, @NotNull BiConsumer<ChannelMember, Throwable> callback) {
        CompletableFuture.supplyAsync(() -> channelMemberDAO.read(channelUUID, playerUUID))
                .whenComplete((loaded, err) -> {
                    callback.accept(loaded, err);
                    if (loaded != null) {
                        cacheMember(loaded);
                    }
                });
    }

    @Override
    public void loadByChannel(@NotNull UUID channelUUID, @NotNull BiConsumer<Collection<ChannelMember>, Throwable> callback) {
        CompletableFuture.supplyAsync(() -> channelMemberDAO.findAllByChannelUUID(channelUUID))
                .whenComplete((loaded, err) -> {
                    callback.accept(loaded, err);
                    ChannelMembersMap channelView = channelViews.computeIfAbsent(channelUUID, uuid -> new ChannelMembersMap(this));
                    populateChannelMembersView(channelView, ChannelMember::getChannelUUID, ChannelMember::getPlayerUUID, playerViews, loaded);
                });
    }

    @Override
    public void loadByPlayer(@NotNull UUID playerUUID, @NotNull BiConsumer<Collection<ChannelMember>, Throwable> callback) {
        CompletableFuture.supplyAsync(() -> channelMemberDAO.findAllByChannelUUID(playerUUID))
                .whenComplete((loaded, err) -> {
                    callback.accept(loaded, err);
                    ChannelMembersMap playerView = playerViews.computeIfAbsent(playerUUID, uuid -> new ChannelMembersMap(this));
                    populateChannelMembersView(playerView, ChannelMember::getPlayerUUID, ChannelMember::getChannelUUID, channelViews, loaded);
                });
    }

    private void populateChannelMembersView(@NotNull ChannelMembersMap primaryView,
                                            @NotNull Function<ChannelMember, UUID> getPrimaryUUID,
                                            @NotNull Function<ChannelMember, UUID> getOtherUUID,
                                            @NotNull Map<UUID, ChannelMembersMap> otherViewsMap,
                                            @NotNull Collection<ChannelMember> members) {
        Function<UUID, ChannelMembersMap> mapProvider = (uuid) -> new ChannelMembersMap(this);
        for (ChannelMember member : members) {
            primaryView.put(getPrimaryUUID.apply(member), member);
            ChannelMembersMap otherView = otherViewsMap.computeIfAbsent(getOtherUUID.apply(member), mapProvider);
            otherView.put(member.getChannelUUID(), member);
        }
    }

    @NotNull
    @Override
    public Iterator<ChannelMember> iterator() {
        return members.iterator();
    }

}
