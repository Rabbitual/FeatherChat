package xyz.mauwh.featherchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.messenger.ChatMessenger;
import xyz.mauwh.featherchat.messenger.ChatMessengers;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        ChatMessenger.player(event.getPlayer()).validateChannels();
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        ChatMessengers messengers = FeatherChat.get().getMessengers();
        messengers.updateAndRemove(ChatMessenger.player(event.getPlayer()));
    }

}
