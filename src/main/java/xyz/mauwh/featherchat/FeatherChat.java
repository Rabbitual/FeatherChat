package xyz.mauwh.featherchat;

import co.aikar.commands.*;
import org.bukkit.plugin.PluginManager;
import xyz.mauwh.featherchat.channel.UserChatChannel;
import xyz.mauwh.featherchat.command.acf.ChatChannelCompletionResolver;
import xyz.mauwh.featherchat.command.acf.ChatChannelContextResolver;
import xyz.mauwh.featherchat.command.FeatherChatChannelSubcommand;
import xyz.mauwh.featherchat.command.acf.FeatherChatBukkitCommandConditions;
import xyz.mauwh.featherchat.listener.PlayerChatListener;
import xyz.mauwh.featherchat.listener.PlayerJoinQuitListener;
import xyz.mauwh.featherchat.messenger.ChatMessengers;
import xyz.mauwh.featherchat.channel.ChatChannelRepository;
import xyz.mauwh.featherchat.command.FeatherChatDefaultCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;
import xyz.mauwh.featherchat.command.FeatherChatDebugSubcommand;

public final class FeatherChat extends JavaPlugin {

    private static FeatherChat instance;

    private BukkitAudiences adventure;
    private BukkitCommandManager manager;
    private ChatChannelRepository channelRepository;
    private ChannelMessageHandler messageHandler;
    private ChatMessengers messengers;

    @Override
    public void onEnable() {
        instance = this;
        this.adventure = BukkitAudiences.create(this);
        this.manager = createCommandManager();
        this.messengers = new ChatMessengers(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(), this);
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
    public static FeatherChat get() throws IllegalStateException {
        instance.validateEnabled();
        return instance;
    }

    @NotNull
    public BukkitAudiences getAudienceProvider() {
        validateEnabled();
        return adventure;
    }

    @NotNull
    public ChatChannelRepository getChannelRepository() {
        validateEnabled();
        return channelRepository;
    }

    @NotNull
    public ChannelMessageHandler getMessageHandler() {
        validateEnabled();
        return messageHandler;
    }

    @NotNull
    public ChatMessengers getMessengers() {
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
        manager.getCommandContexts().registerContext(UserChatChannel.class, new ChatChannelContextResolver(channelRepository));
        manager.getCommandCompletions().registerCompletion("channels", new ChatChannelCompletionResolver(channelRepository));

        final var conditions = manager.getCommandConditions();
        conditions.addCondition("playerOnly", FeatherChatBukkitCommandConditions.PLAYER_ONLY);
        conditions.addCondition(UserChatChannel.class, "isMember", FeatherChatBukkitCommandConditions.IS_MEMBER);
        conditions.addCondition(UserChatChannel.class, "isOwner", FeatherChatBukkitCommandConditions.IS_OWNER);
        conditions.addCondition(String.class, "channelName", FeatherChatBukkitCommandConditions.CHANNEL_NAME);
        conditions.addCondition(String.class, "charLimit", FeatherChatBukkitCommandConditions.CHAR_LIMIT);

        manager.registerCommand(new FeatherChatDefaultCommand(this));
        manager.registerCommand(new FeatherChatDebugSubcommand(this));
        manager.registerCommand(new FeatherChatChannelSubcommand(channelRepository));
        return manager;
    }

}
