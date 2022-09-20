package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengerFactory;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;
import xyz.mauwh.featherchat.store.yaml.YamlPlayerDAO;

import java.io.File;
import java.util.*;

public final class ChatMessengerRepository<T, U extends ChatMessenger<T>, V extends Player<T>> implements ChatMessengers<T, U, V> {

    private final ChatMessengerFactory<T, U, V> messengerFactory;
    private final Map<T, U> sender2messenger;
    private final Map<UUID, V> uuid2player;
    private final U console;
    private final YamlPlayerDAO<T, U, V> playerDao;

    public ChatMessengerRepository(@NotNull File dataFolder, @NotNull ChatMessengerFactory<T, U, V> messengerFactory) {
        this.messengerFactory = messengerFactory;
        this.sender2messenger = new WeakHashMap<>();
        this.uuid2player = new HashMap<>();
        this.console = messengerFactory.console();
        this.console.setDisplayName(Component.text("Console", NamedTextColor.GOLD));
        this.playerDao = new YamlPlayerDAO<>(dataFolder, messengerFactory);
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public U getBySender(@NotNull T sender) {
        U messenger = sender2messenger.get(sender);
        if (messenger != null) {
            return messenger;
        }

        messenger = messengerFactory.sender(sender);
        if (!(messenger).isPlayer()) {
            return messenger;
        }

        UUID uuid = ((V)messenger).getUUID();
        V byUUID = uuid2player.get(uuid);
        V finalMessenger = (V)messenger;
        return (U)Objects.requireNonNullElseGet(byUUID, () -> {
            try {
                return playerDao.read(uuid);
            } catch (DataEntityAccessException err) {
                playerDao.create(finalMessenger);
                return finalMessenger;
            }
        });
    }

    @NotNull
    public V getByUUID(@NotNull UUID uuid) {
        return uuid2player.computeIfAbsent(uuid, playerDao::read);
    }

    @Override
    public void update(@NotNull V player) {
        playerDao.update(player);
    }

    @Override
    public void updateAndRemove(@NotNull V player) {
        uuid2player.remove(player.getUUID());
        sender2messenger.remove(player.getHandle(), player);
        playerDao.update(player);
    }

    @Override
    @NotNull
    public U getConsole() {
        return console;
    }

}