package xyz.mauwh.featherchat.bukkit.messenger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.bukkit.FeatherChatBukkit;
import xyz.mauwh.featherchat.messenger.ChatMessengerFactory;

import java.util.UUID;

public class BukkitChatMessengerFactory extends ChatMessengerFactory<CommandSender, BukkitChatMessenger, BukkitPlayer> {

    private final FeatherChatBukkit plugin;
    private final BukkitChatMessenger console;

    public BukkitChatMessengerFactory(@NotNull FeatherChatBukkit plugin) {
        this.plugin = plugin;
        this.console = new BukkitChatMessenger(plugin, Bukkit.getConsoleSender());
    }

    @Override
    @NotNull
    public BukkitChatMessenger console() {
        return console;
    }

    @NotNull
    public BukkitChatMessenger sender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player player) {
            return new BukkitPlayer(plugin, player);
        }
        return new BukkitChatMessenger(plugin, sender);
    }

    @Override
    @NotNull
    public BukkitPlayer player(@NotNull UUID player) throws IllegalArgumentException {
        org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) {
            return new BukkitPlayer(plugin, player);
        }
        return new BukkitPlayer(plugin, bukkitPlayer);
    }

}
