package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.FeatherChat;

import java.util.Optional;
import java.util.UUID;

public interface ChatMessenger extends Identified {
    @NotNull
    static ChatMessenger console() {
        return FeatherChat.get().getMessengers().getConsole();
    }

    @NotNull
    static ChatMessenger sender(@NotNull CommandSender sender) {
        return FeatherChat.get().getMessengers().get(sender);
    }

    @NotNull
    static Player player(@NotNull org.bukkit.entity.Player player) {
        return player(player.getUniqueId());
    }

    @NotNull
    static Player player(@NotNull UUID player) {
        return FeatherChat.get().getMessengers().get(player);
    }

    @NotNull String getName();

    @NotNull Optional<Component> getDisplayName();

    @NotNull Component getFriendlyName();

    void setDisplayName(@Nullable Component displayName);

    @Nullable CommandSender getHandle();

    boolean isPlayer();

    void sendMessage(@Nullable ChatMessenger sender, @NotNull Component message);

    void sendMessage(@NotNull Component message);
}
