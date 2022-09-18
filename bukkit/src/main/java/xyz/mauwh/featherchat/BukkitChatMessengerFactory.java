package xyz.mauwh.featherchat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessengerFactory;
import xyz.mauwh.featherchat.messenger.BukkitChatMessenger;
import xyz.mauwh.featherchat.messenger.BukkitPlayer;

import java.util.Objects;
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
    public BukkitPlayer player(@NotNull UUID player) {
        return new BukkitPlayer(plugin, Objects.requireNonNull(Bukkit.getPlayer(player)));
    }

    @Override
    @NotNull
    public BukkitPlayer offlinePlayer(@NotNull UUID player, String name) {
        return new BukkitPlayer(plugin, player, name);
    }

}
