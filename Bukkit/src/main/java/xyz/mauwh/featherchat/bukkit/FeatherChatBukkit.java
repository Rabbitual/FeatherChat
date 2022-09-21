package xyz.mauwh.featherchat.bukkit;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandManager;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.ChatChannels;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.bukkit.listener.PlayerChatListener;
import xyz.mauwh.featherchat.bukkit.listener.PlayerJoinQuitListener;
import xyz.mauwh.featherchat.channel.ChatChannelRepository;
import xyz.mauwh.featherchat.channel.invite.ChannelInvitationsImpl;
import xyz.mauwh.featherchat.command.FeatherChatChannelSubcommand;
import xyz.mauwh.featherchat.command.FeatherChatDebugSubcommand;
import xyz.mauwh.featherchat.command.FeatherChatDefaultCommand;
import xyz.mauwh.featherchat.command.acf.FeatherChatCommandCompletionHandler;
import xyz.mauwh.featherchat.command.acf.FeatherChatCommandConditions;
import xyz.mauwh.featherchat.command.acf.FeatherChatContextResolvers;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitChatMessenger;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitChatMessengerFactory;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitPlayer;
import xyz.mauwh.featherchat.messenger.ChatMessengerRepository;
import xyz.mauwh.featherchat.plugin.FeatherChatAccessible;

public final class FeatherChatBukkit extends JavaPlugin implements FeatherChatAccessible {

    private static FeatherChatBukkit instance;
    private ChannelMessageHandler messageHandler;
    private ChatChannels channels;
    private ChannelInvitations invitations;
    private ChatMessengers<CommandSender, BukkitChatMessenger, BukkitPlayer> messengers;
    private BukkitAudiences adventure;
    private BukkitCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        this.channels = new ChatChannelRepository(this);
        this.invitations = new ChannelInvitationsImpl();

        final BukkitChatMessengerFactory messengerFactory = new BukkitChatMessengerFactory(this);
        this.messengers = new ChatMessengerRepository<>(getDataFolder(), messengerFactory);

        this.messageHandler = new ChannelMessageHandler();

        this.adventure = BukkitAudiences.create(this);
        this.commandManager = new BukkitCommandManager(this);
        setupCommandManager(commandManager);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(messengers), this);
        pm.registerEvents(new PlayerChatListener(this), this);

        Bukkit.getOnlinePlayers().forEach(player -> ((BukkitPlayer)getMessengers().getBySender(player)).validateChannels());
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.commandManager.unregisterCommands();
        this.commandManager = null;
        this.messageHandler = null;
        this.channels = null;
        instance = null;
    }

    @NotNull
    public static FeatherChatBukkit get() throws IllegalStateException {
        instance.validateEnabled();
        return instance;
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
        return channels;
    }

    @Override
    @NotNull
    public ChannelInvitations getInvitations() {
        validateEnabled();
        return invitations;
    }

    @Override
    @NotNull
    public String getVersion() {
        validateEnabled();
        return getDescription().getVersion();
    }

    @Override
    @NotNull
    public AudienceProvider getAdventure() {
        validateEnabled();
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

    private void setupCommandManager(@NotNull CommandManager<?, ?, ?, ?, ?, ?> commandManager) {
        FeatherChatContextResolvers contextResolvers = new FeatherChatContextResolvers(messengers, channels);
        commandManager.getCommandContexts().registerIssuerOnlyContext(Player.class, contextResolvers::getPlayer);
        commandManager.getCommandContexts().registerContext(UserChatChannel.class, contextResolvers::getUserChatChannel);
        commandManager.getCommandCompletions().registerCompletion("channels", new FeatherChatCommandCompletionHandler(this)::getCompletions);

        final var conditions = commandManager.getCommandConditions();
        conditions.addCondition("playerOnly", FeatherChatCommandConditions::playerOnly);
        conditions.addCondition(UserChatChannel.class, "isMember", FeatherChatCommandConditions::isMember);
        conditions.addCondition(UserChatChannel.class, "isOwner", FeatherChatCommandConditions::isOwner);
        conditions.addCondition(String.class, "channelName", FeatherChatCommandConditions::channelName);
        conditions.addCondition(String.class, "charLimit", FeatherChatCommandConditions::charLimit);

        commandManager.registerCommand(new FeatherChatDefaultCommand(this));
        commandManager.registerCommand(new FeatherChatDebugSubcommand(this));
        commandManager.registerCommand(new FeatherChatChannelSubcommand(this));
    }

}
