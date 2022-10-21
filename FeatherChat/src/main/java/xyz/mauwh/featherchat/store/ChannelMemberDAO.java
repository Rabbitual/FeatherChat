package xyz.mauwh.featherchat.store;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;

import java.util.Collection;
import java.util.UUID;

public interface ChannelMemberDAO {
    void create(@NotNull ChannelMember member);
    @NotNull ChannelMember read(@NotNull UUID channelUUID, @NotNull UUID playerUUID);
    void update(@NotNull ChannelMember member);
    void delete(@NotNull ChannelMember member);
    @NotNull Collection<ChannelMember> findAllByChannelUUID(@NotNull UUID channelUUID);
    @NotNull Collection<ChannelMember> findAllByPlayerUUID(@NotNull UUID playerUUID);
}
