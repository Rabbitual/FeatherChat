package xyz.mauwh.featherchat;

import co.aikar.commands.BukkitCommandManager;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.command.*;
import xyz.mauwh.featherchat.listener.PlayerChatListener;
import xyz.mauwh.featherchat.listener.PlayerJoinQuitListener;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;
import xyz.mauwh.featherchat.messenger.BukkitChatMessenger;
import xyz.mauwh.featherchat.messenger.BukkitPlayer;
import xyz.mauwh.featherchat.messenger.ChatMessengerRepository;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

public final class FeatherChatBukkit extends JavaPlugin implements FeatherChatAccessible {

    private static FeatherChatBukkit instance;

    private BukkitChatMessengerFactory messengerFactory;
    private BukkitAudiences adventure;
    private BukkitCommandManager manager;
    private ChatChannels channelRepository;
    private ChannelMessageHandler messageHandler;
    private ChatMessengers<CommandSender, BukkitChatMessenger, BukkitPlayer> messengers;

    @Override
    public void onEnable() {
        instance = this;
        this.messengerFactory = new BukkitChatMessengerFactory(this);
        this.adventure = BukkitAudiences.create(this);
        this.manager = createCommandManager();
        this.messengers = new ChatMessengerRepository<>(getDataFolder(), messengerFactory);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(messengers), this);
        pm.registerEvents(new PlayerChatListener(this), this);
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.manager.unregisterCommands();
        this.manager = null;
        this.messageHandler = null;
        this.channelRepository = null;
        instance = null;
    }

    @NotNull
    public static FeatherChatBukkit get() throws IllegalStateException {
        instance.validateEnabled();
        return instance;
    }

    @Override
    @NotNull
    public BukkitChatMessengerFactory getMessengerFactory() {
        return messengerFactory;
    }

    @NotNull
    public BukkitAudiences getAudienceProvider() {
        validateEnabled();
        return adventure;
    }

    @Override
    @NotNull
    public ChatChannels getChannels() {
        validateEnabled();
        return channelRepository;
    }

    @Override
    @NotNull
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    @NotNull
    public AudienceProvider getAdventure() {
        return adventure;
    }

    @Override
    @NotNull
    public ChannelMessageHandler getMessageHandler() {
        validateEnabled();
        return messageHandler;
    }

    @Override
    @NotNull
    public ChatMessengers<CommandSender, BukkitChatMessenger, BukkitPlayer> getMessengers() {
        validateEnabled();
        return messengers;
    }

    private void validateEnabled() throws IllegalStateException {
        if (!instance.isEnabled()) {
            throw new IllegalStateException("Unable to access plugin before it has been enabled");
        }
    }

    @NotNull
    private BukkitCommandManager createCommandManager() {
        manager = new BukkitCommandManager(this);
        manager.getCommandContexts().registerContext(UserChatChannel.class, new BukkitChatChannelContextResolver(channelRepository));
        manager.getCommandCompletions().registerCompletion("channels", new BukkitChatChannelCompletionResolver(this));

        final var conditions = manager.getCommandConditions();
        conditions.addCondition("playerOnly", FeatherChatBukkitCommandConditions.PLAYER_ONLY);
        conditions.addCondition(UserChatChannel.class, "isMember", FeatherChatBukkitCommandConditions.IS_MEMBER);
        conditions.addCondition(UserChatChannel.class, "isOwner", FeatherChatBukkitCommandConditions.IS_OWNER);
        conditions.addCondition(String.class, "channelName", FeatherChatBukkitCommandConditions.CHANNEL_NAME);
        conditions.addCondition(String.class, "charLimit", FeatherChatBukkitCommandConditions.CHAR_LIMIT);

        manager.registerCommand(new FeatherChatDefaultCommand(this));
        manager.registerCommand(new FeatherChatDebugSubcommand(this));
        manager.registerCommand(new FeatherChatChannelSubcommand(this));
        return manager;
    }

}
