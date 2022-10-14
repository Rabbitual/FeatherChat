package xyz.mauwh.featherchat.bukkit.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.messenger.PlayerAccessible;

public class PlayerJoinQuitListener implements Listener {

    private final ChatMessengers<CommandSender> messengers;

    public PlayerJoinQuitListener(@NotNull ChatMessengers<CommandSender> messengers) {
        this.messengers = messengers;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        ((PlayerAccessible)messengers.getBySender(event.getPlayer())).validateChannels();
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        messengers.updateAndRemove((Player)messengers.getBySender(event.getPlayer()));
    }

}
