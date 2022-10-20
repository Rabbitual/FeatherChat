package xyz.mauwh.featherchat.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.NamespacedChannelKey;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class UserChatChannelImpl extends AbstractChatChannel implements UserChatChannel {

    private UUID owner;
    private final Set<UUID> members;

    public UserChatChannelImpl(@NotNull FeatherChatPlugin plugin, @NotNull UUID uuid, @NotNull NamespacedChannelKey key, @NotNull UUID owner, @NotNull String name) {
        this(plugin, uuid, key, owner, name, new HashSet<>());
    }

    public UserChatChannelImpl(@NotNull FeatherChatPlugin plugin, @NotNull UUID uuid, @NotNull NamespacedChannelKey key, @NotNull UUID owner, @NotNull String name, @NotNull Set<UUID> members) {
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
    public void setOwner(@NotNull Player owner) {
        this.owner = owner.getUUID();
    }

    @Override
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setMembers(@NotNull Collection<UUID> members) {
        this.members.clear();
        this.members.addAll(members);
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
    public boolean isMember(@NotNull Player member) {
        return member.isPlayer() && isMember(member.getUUID());
    }

    @Override
    public boolean addMember(@NotNull Player player) {
        if (members.add(player.getUUID())) {
            plugin.getChannels().updateChannel(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMember(@NotNull Player player) {
        if (members.remove(player.getUUID())) {
            plugin.getChannels().updateChannel(this);
            return true;
        }
        return false;
    }

    public void sendDissolutionMessage() {
        Component nameAndKey = getDisplayName().append(text(" (" + getKey() + ")"));
        Component message = nameAndKey.append(text(" has been dissolved", RED));
        plugin.getMessengers().getAll().stream().filter(this::isMember).forEach(player -> player.sendMessage(message));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && owner.equals(((UserChatChannelImpl)o).owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID(), getKey(), getName(), owner);
    }

}
