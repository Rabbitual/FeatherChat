package xyz.mauwh.featherchat.channel.member;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.member.ChannelMember;
import xyz.mauwh.featherchat.api.channel.member.ChannelMembers;

import java.util.*;

public class ChannelMembersMap implements Iterable<ChannelMember> {

    private final ChannelMemberRepository globalRepo;
    final Map<UUID, ChannelMember> members;

    public ChannelMembersMap(@NotNull ChannelMemberRepository globalRepo) {
        this.globalRepo = globalRepo;
        this.members = new HashMap<>();
    }

    @NotNull
    public ChannelMembers.Global global() {
        return globalRepo;
    }

    public void put(@NotNull UUID uuid, @NotNull ChannelMember member) {
        members.put(uuid, member);
    }

    public boolean remove(@NotNull UUID uuid) {
        return members.remove(uuid) != null;
    }

    @Nullable
    public ChannelMember get(@NotNull UUID uuid) {
        return members.get(uuid);
    }

    @NotNull
    public Collection<ChannelMember> getAll() {
        return members.values();
    }

    @Override
    @NotNull
    public Iterator<ChannelMember> iterator() {
        return new Iterator<>() {
            private final Iterator<Map.Entry<UUID, ChannelMember>> iter = members.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public ChannelMember next() {
                return iter.next().getValue();
            }
        };
    }

}
