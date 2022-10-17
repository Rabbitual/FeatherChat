package xyz.mauwh.featherchat.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.UUID;

public interface ChatChannelDAO {
    void create(@NotNull UserChatChannel channel);
    @Nullable UserChatChannel read(@NotNull UUID channelUUID);
    void update(@NotNull UserChatChannel channel);
    void delete(@NotNull UserChatChannel channel);
}
