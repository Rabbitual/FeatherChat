package xyz.mauwh.featherchat.channel;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface ChannelAccessible {
    void setMembers(@NotNull Set<UUID> members);
}
