package xyz.mauwh.featherchat.bukkit;

import co.aikar.commands.*;
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
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;
import xyz.mauwh.featherchat.bukkit.listener.PlayerChatListener;
import xyz.mauwh.featherchat.bukkit.listener.PlayerJoinQuitListener;
import xyz.mauwh.featherchat.bukkit.scheduler.FeatherChatBukkitScheduler;
import xyz.mauwh.featherchat.bukkit.scheduler.FeatherChatBukkitTask;
import xyz.mauwh.featherchat.channel.ChatChannelRepository;
import xyz.mauwh.featherchat.channel.invite.ChannelInvitationsImpl;
import xyz.mauwh.featherchat.command.FeatherChatChannelSubcommand;
import xyz.mauwh.featherchat.command.FeatherChatDebugSubcommand;
import xyz.mauwh.featherchat.command.FeatherChatDefaultCommand;
import xyz.mauwh.featherchat.command.acf.FeatherChatCommandCompletionHandler;
import xyz.mauwh.featherchat.command.acf.FeatherChatCommandConditions;
import xyz.mauwh.featherchat.command.acf.FeatherChatContextResolvers;
import xyz.mauwh.featherchat.message.ChannelMessageHandler;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitNonPlayerMessengerImpl;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitChatMessengerFactory;
import xyz.mauwh.featherchat.bukkit.messenger.BukkitPlayerImpl;
import xyz.mauwh.featherchat.messenger.ChatMessengerRepository;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;
import xyz.mauwh.featherchat.scheduler.FeatherChatScheduler;

import java.util.Objects;

public final class FeatherChatBukkit extends JavaPlugin implements FeatherChatPlugin {

    private static FeatherChatBukkit instance;
    private ChannelMessageHandler messageHandler;
    private ChatChannels channels;
    private ChannelInvitations invitations;
    private ChatMessengers<CommandSender> messengers;
    private BukkitAudiences adventure;
    private BukkitCommandManager commandManager;

    @Override
    public void onEnable() {
        this.enable();
    }

    @Override
    public void enable() {
        instance = this;
        final BukkitChatMessengerFactory messengerFactory = new BukkitChatMessengerFactory(this);
        this.adventure = BukkitAudiences.create(this);

        FeatherChatScheduler<FeatherChatBukkitTask> scheduler = new FeatherChatBukkitScheduler(this);
        this.invitations = new ChannelInvitationsImpl<>(scheduler);
        this.channels = new ChatChannelRepository(this);
        this.messengers = new ChatMessengerRepository<>(getDataFolder(), messengerFactory);
        this.messageHandler = new ChannelMessageHandler(messengers);
        this.commandManager = new BukkitCommandManager(this);
        setupCommandManager(commandManager);


        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(messengers), this);
        pm.registerEvents(new PlayerChatListener(this), this);

        Bukkit.getOnlinePlayers().forEach(player -> messengers.getByUUID(player.getUniqueId()).validateChannels());
    }

    @Override
    public void onDisable() {
        this.disable();
    }

    @Override
    public void disable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.commandManager.unregisterCommands();
        this.commandManager = null;
        this.messageHandler = null;
        this.channels = null;
        this.invitations = null;
        this.messengers = null;
        instance = null;
    }

    @NotNull
    public static FeatherChatBukkit get() throws IllegalStateException {
        Objects.requireNonNull(instance, "null instance");
        if (!instance.isEnabled()) {
            throw new IllegalStateException("Unable to access plugin before it has been enabled");
        }
        return instance;
    }

    @NotNull
    public BukkitAudiences getAudienceProvider() {
        return adventure;
    }

    @Override
    @NotNull
    public ChatChannels getChannels() {
        return channels;
    }

    @Override
    @NotNull
    public ChannelInvitations getInvitations() {
        return invitations;
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
        return messageHandler;
    }

    @Override
    @NotNull
    public ChatMessengers<CommandSender> getMessengers() {
        return messengers;
    }

    @Override
    public <IT, I extends CommandIssuer,
            CEC extends CommandExecutionContext<CEC, I>,
            CC extends ConditionContext<I>> void setupCommandManager(@NotNull CommandManager<IT, I, ?, ?, CEC, CC> commandManager) {
        FeatherChatContextResolvers contextResolvers = new FeatherChatContextResolvers(this);
        commandManager.getCommandContexts().registerIssuerOnlyContext(ChatMessenger.class, contextResolvers::getMessenger);
        commandManager.getCommandContexts().registerIssuerAwareContext(Player.class, contextResolvers::getPlayer);
        commandManager.getCommandContexts().registerContext(UserChatChannel.class, contextResolvers::getUserChatChannel);
        commandManager.getCommandCompletions().registerCompletion("channels", new FeatherChatCommandCompletionHandler(this)::getChannelMatches);

        FeatherChatCommandConditions fcConditions = new FeatherChatCommandConditions(messengers, invitations);
        CommandConditions<I, CEC, CC> conditions = commandManager.getCommandConditions();
        conditions.addCondition("playerOnly", fcConditions::playerOnly);
        conditions.addCondition(String.class, "channelName", fcConditions::channelName);
        conditions.addCondition(String.class, "charLimit", fcConditions::charLimit);

        commandManager.registerCommand(new FeatherChatDefaultCommand(this));
        commandManager.registerCommand(new FeatherChatDebugSubcommand(channels));
        commandManager.registerCommand(new FeatherChatChannelSubcommand(this));
    }

}
