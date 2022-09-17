package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.store.yaml.YamlPlayerDAO;

import java.util.*;

public final class ChatMessengers {

    private final FeatherChat plugin;
    private final Map<CommandSender, ChatMessenger> sender2messenger;
    private final Map<UUID, Player> uuid2player;
    private final ChatMessenger console;
    private final YamlPlayerDAO playerDao;

    public ChatMessengers(@NotNull FeatherChat plugin) {
        this.plugin = plugin;
        this.sender2messenger = new WeakHashMap<>();
        this.uuid2player = new HashMap<>();
        this.console = new ChatMessengerImpl(plugin, Bukkit.getConsoleSender());
        this.console.setDisplayName(Component.text("Console", NamedTextColor.GOLD));
        this.playerDao = new YamlPlayerDAO(plugin);
    }

    @NotNull
    public ChatMessenger get(@NotNull CommandSender sender) {
        return sender2messenger.computeIfAbsent(sender, sender1 -> {
            if (!(sender instanceof org.bukkit.entity.Player bukkitPlayer)) {
                return new ChatMessengerImpl(plugin, sender);
            }
            Player byUUID = uuid2player.get(bukkitPlayer.getUniqueId());
            return Objects.requireNonNullElseGet(byUUID, () -> {
                try {
                    return playerDao.read(bukkitPlayer.getUniqueId());
                } catch (DataEntityAccessException err) {
                    Player player = new ChatMessengerPlayerImpl(plugin, bukkitPlayer);
                    playerDao.create(player);
                    return player;
                }
            });
        });
    }

    @NotNull
    public Player get(@NotNull UUID uuid) {
        return uuid2player.computeIfAbsent(uuid, playerDao::read);
    }

    public void update(@NotNull Player player) {
        playerDao.update(player);
    }

    public void updateAndRemove(@NotNull Player player) {
        uuid2player.remove(player.getUUID());
        sender2messenger.remove(player.getHandle(), player);
        playerDao.update(player);
    }

    @NotNull
    public ChatMessenger getConsole() {
        return console;
    }

}
