package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;
import xyz.mauwh.featherchat.store.yaml.YamlPlayerDAO;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class ChatMessengerRepository<T> implements ChatMessengers<T> {

    private final ChatMessengerFactory<T> messengerFactory;
    private final Map<T, ChatMessenger> sender2messenger;
    private final Map<String, Player> name2player;
    private final Map<UUID, Player> uuid2player;
    private final ChatMessenger console;
    private final YamlPlayerDAO playerDao;

    public ChatMessengerRepository(@NotNull FeatherChatPlugin plugin,
                                   @NotNull ChatMessengerFactory<T> messengerFactory) {
        this.messengerFactory = messengerFactory;
        this.sender2messenger = new WeakHashMap<>();
        this.name2player = new HashMap<>();
        this.uuid2player = new HashMap<>();
        this.console = messengerFactory.console();
        this.console.setDisplayName(text("Feather", AQUA, TextDecoration.BOLD).append(text("Chat", GOLD, TextDecoration.BOLD)));
        this.playerDao = new YamlPlayerDAO(plugin.getDataFolder(), messengerFactory);
    }

    @Override
    @Nullable
    public ChatMessenger getBySender(@NotNull T sender) {
        return sender2messenger.get(sender);
    }

    @Override
    @Nullable
    public Player getByName(@NotNull String name) {
        return name2player.get(name);
    }

    @Override
    @Nullable
    public Player getByUUID(@NotNull UUID uuid) {
        return uuid2player.get(uuid);
    }

    @NotNull
    public Collection<Player> getAll() {
        return Collections.unmodifiableCollection(uuid2player.values());
    }

    @Override
    public void loadPlayer(@NotNull UUID uuid, @Nullable String name, @NotNull Consumer<Throwable> exHandler, @Nullable Consumer<Player> callback, boolean async) {
        Player cached = uuid2player.get(uuid);
        if (cached != null) {
            if (callback != null) {
                callback.accept(cached);
            }
            return;
        }

        Function<Throwable, Player> createPlayer = err -> {
            Player player = name == null ? messengerFactory.player(uuid) : messengerFactory.player(uuid, name);
            playerDao.create(player);
            return player;
        };

        if (!async) {
            Player player;
            try {
                player = playerDao.read(uuid);
            } catch (DataEntityAccessException err) {
                try {
                    player = createPlayer.apply(err);
                } catch (DataEntityAccessException err1) {
                    exHandler.accept(err1);
                    return;
                }
            }
            Player loaded = player;
            if (callback != null) {
                callback.accept(loaded);
            }
            cachePlayer(loaded);
            return;
        }

        CompletableFuture.supplyAsync(() -> playerDao.read(uuid))
                .exceptionallyAsync(createPlayer)
                .whenComplete((loaded, err) -> {
                    if (err != null) {
                        exHandler.accept(err);
                    } else if (loaded != null) {
                        if (callback != null) {
                            callback.accept(loaded);
                        }
                        cachePlayer(loaded);
                    }
                });
    }

    @Override
    public void update(@NotNull xyz.mauwh.featherchat.api.messenger.Player player) {
        playerDao.update(player);
    }

    @Override
    public void cachePlayer(@NotNull Player player) {
        T handle = player.getHandle();
        if (player.getHandle() != null) {
            sender2messenger.put(handle, player);
        }
        name2player.put(player.getName(), player);
        uuid2player.put(player.getUUID(), player);
    }

    @Override
    public void updateAndRemove(@NotNull xyz.mauwh.featherchat.api.messenger.Player player) {
        uuid2player.remove(player.getUUID());
        name2player.remove(player.getName());
        //noinspection SuspiciousMethodCalls
        sender2messenger.remove(player.getHandle(), player);
        playerDao.update(player);
    }

    @Override
    @NotNull
    public xyz.mauwh.featherchat.api.messenger.ChatMessenger getConsole() {
        return console;
    }

}
