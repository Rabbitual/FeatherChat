package xyz.mauwh.featherchat.bukkit.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.Objects;

public class PlayerJoinQuitListener implements Listener {

    private final ChatMessengers<CommandSender> messengers;

    public PlayerJoinQuitListener(@NotNull ChatMessengers<CommandSender> messengers) {
        this.messengers = messengers;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull AsyncPlayerPreLoginEvent event) {
        Player player = messengers.getByUUID(event.getUniqueId());
        if (player != null) {
            player.validateChannels();
            return;
        }

        messengers.loadPlayer(event.getUniqueId(), event.getName(), (loaded, err) -> {
            if (err != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Unable to load player data - please check the console");
            }
            loaded.validateChannels();
        }, false);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = (Player)messengers.getBySender(event.getPlayer());
        Objects.requireNonNull(player, "How did you fuck this up?");
        messengers.updateAndRemove(player);
    }

}
