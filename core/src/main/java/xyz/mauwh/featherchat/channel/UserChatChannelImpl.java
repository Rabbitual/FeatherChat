package xyz.mauwh.featherchat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserChatChannelImpl extends AbstractChatChannel implements UserChatChannel {

    private UUID owner;
    private final Set<UUID> members;

    public UserChatChannelImpl(@NotNull FeatherChatAccessible plugin, @NotNull UUID uuid, @NotNull NamespacedChannelKey key, @NotNull UUID owner, @NotNull String name) {
        this(plugin, uuid, key, owner, name, new HashSet<>());
    }

    public UserChatChannelImpl(@NotNull FeatherChatAccessible plugin, @NotNull UUID uuid, @NotNull NamespacedChannelKey key, @NotNull UUID owner, @NotNull String name, @NotNull Set<UUID> members) {
        super(plugin, key, uuid, name);
        this.setOwner(owner);
        this.members = members;
        this.members.add(owner);
    }

    @Override
    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    @Override
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @Override
    @NotNull
    public Set<UUID> getMembers() {
        return Set.copyOf(members);
    }

    @Override
    public boolean isMember(@NotNull UUID member) {
        return members.contains(member);
    }

    @Override
    public boolean isMember(@NotNull ChatMessenger<?> member) {
        return members.contains(member);
    }

    public boolean addMember(@NotNull Player player) {
        return members.add(player.getUUID());
    }

    public boolean removeMember(@NotNull Player player) {
        return members.remove(player.getUUID());
    }

    public void sendDissolutionMessage() {
        Player owner = plugin.getMessengers().getByUUID(this.owner);
        Component dissolutionMsg = Component.text(owner.getName() + "has dissolved '", NamedTextColor.RED)
                .append(getFriendlyName()).append(Component.text("'", NamedTextColor.RED));
        getMembers().stream().map(plugin.getMessengers()::getByUUID).filter(Player::isOnline).forEach(recipient -> recipient.sendMessage(dissolutionMsg));
        owner.sendMessage(Component.text("You have dissolved your channel '", NamedTextColor.RED)
                .append(getFriendlyName()).append(Component.text("'", NamedTextColor.RED)));
    }

}
