package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.store.yaml.YamlPlayerDAO;

import java.io.File;
import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class ChatMessengerRepository<T, U extends ChatMessenger, V extends Player> implements ChatMessengers<T, U, V> {

    private final ChatMessengerFactory<T, U, V> messengerFactory;
    private final Map<T, U> sender2messenger;
    private final Map<String, V> name2player;
    private final Map<UUID, V> uuid2player;
    private final U console;
    private final YamlPlayerDAO<T, U, V> playerDao;

    public ChatMessengerRepository(@NotNull File dataFolder, @NotNull ChatMessengerFactory<T, U, V> messengerFactory) {
        this.messengerFactory = messengerFactory;
        this.sender2messenger = new WeakHashMap<>();
        this.name2player = new HashMap<>();
        this.uuid2player = new HashMap<>();
        this.console = messengerFactory.console();
        this.console.setDisplayName(text("Feather", AQUA, TextDecoration.BOLD).append(text("Chat", GOLD, TextDecoration.BOLD)));
        this.playerDao = new YamlPlayerDAO<>(dataFolder, messengerFactory);
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public U getBySender(@NotNull T sender) {
        U messenger = sender2messenger.get(sender); // try get cached messenger by sender
        if (messenger != null) {
            return messenger;
        }

        messenger = messengerFactory.sender(sender); //
        if (!(messenger).isPlayer()) {
            return messenger;
        }

        UUID uuid = ((V)messenger).getUUID();
        return (U)readOrCreate(uuid);
    }

    @Nullable
    public V getByName(@NotNull String name) {
        return name2player.get(name);
    }

    @NotNull
    public V getByUUID(@NotNull UUID uuid) {
        V player = uuid2player.get(uuid);
        if (player != null) {
            return player;
        }
        return readOrCreate(uuid);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private V readOrCreate(@NotNull UUID uuid) throws IllegalArgumentException {
        V player = uuid2player.get(uuid);
        if (player != null) {
            return player;
        }

        try {
            player = playerDao.read(uuid);
        } catch (DataEntityAccessException err) {
            player = messengerFactory.player(uuid);
            if (!player.isOnline()) {
                throw new IllegalArgumentException("Unable to create player data for offline player");
            }
            playerDao.create(player);
        }

        if (player.getHandle() != null) {
            sender2messenger.put(player.getHandle(), (U)player);
        }
        name2player.put(player.getName(), player);
        uuid2player.put(uuid, player);
        return player;
    }

    @Override
    public void update(@NotNull V player) {
        playerDao.update(player);
    }

    @Override
    public void updateAndRemove(@NotNull V player) {
        uuid2player.remove(player.getUUID());
        name2player.remove(player.getName());
        if (player.getHandle() != null) {
            //noinspection SuspiciousMethodCalls
            sender2messenger.remove(player.getHandle(), player);
        }
        playerDao.update(player);
    }

    @Override
    @NotNull
    public U getConsole() {
        return console;
    }

}
