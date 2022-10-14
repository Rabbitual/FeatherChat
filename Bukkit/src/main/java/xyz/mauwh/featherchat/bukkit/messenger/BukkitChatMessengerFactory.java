package xyz.mauwh.featherchat.bukkit.messenger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.messenger.ChatMessengerFactory;

import java.util.UUID;

public class BukkitChatMessengerFactory extends ChatMessengerFactory<CommandSender> {

    private final FeatherChatBukkit plugin;
    private final BukkitNonPlayerMessengerImpl console;

    public BukkitChatMessengerFactory(@NotNull FeatherChatBukkit plugin) {
        this.plugin = plugin;
        this.console = new BukkitNonPlayerMessengerImpl(plugin, Bukkit.getConsoleSender());
    }

    @Override
    @NotNull
    public ChatMessenger console() {
        return console;
    }

    @NotNull
    public ChatMessenger sender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player player) {
            return new BukkitPlayerImpl(plugin, player);
        }
        return new BukkitNonPlayerMessengerImpl(plugin, sender);
    }

    @Override
    @NotNull
    public Player player(@NotNull UUID uuid) throws IllegalArgumentException {
        org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Player is offline - please use ChatMessengerFactory#player(java.util.UUID, String)");
        }
        return new BukkitPlayerImpl(plugin, player);
    }

    @Override
    @NotNull
    public Player player(@NotNull UUID player, @NotNull String name) {
        return new BukkitPlayerImpl(plugin, player, name);
    }

}
