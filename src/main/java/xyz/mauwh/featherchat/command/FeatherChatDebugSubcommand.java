package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.mauwh.featherchat.FeatherChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.channel.ChatChannel;
import xyz.mauwh.featherchat.messenger.ChatMessenger;
import xyz.mauwh.featherchat.messenger.Player;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@CommandAlias("featherchat|fc")
@Subcommand("debug")
@CommandPermission("featherchat.debug")
public final class FeatherChatDebugSubcommand extends BaseCommand {

    private final ChatChannel debugChannel;

    public FeatherChatDebugSubcommand(@NotNull FeatherChat plugin) {
        this.debugChannel = plugin.getChannelRepository().getDebugChannel();
    }

    @Subcommand("chat")
    public void onDebugChat(@NotNull CommandIssuer sender, @NotNull String message) {
        debugChannel.sendMessage(ChatMessenger.sender(sender.getIssuer()), Component.text(message));
    }

    @Subcommand("format")
    public void onDebugFormat(@NotNull String format) {
        debugChannel.setMessageFormat(format);
    }

    @Subcommand("displayname")
    public void onDebugName(@Single @NotNull String name) {
        debugChannel.setDisplayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    private final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("cccc, MMMM d, yyyy", Locale.US);
    private final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("h:mm a (OOOO)", Locale.US);

    @Subcommand("datetest")
    public void onDateTest(@NotNull CommandIssuer issuer) {
        LocalDateTime ldt = LocalDateTime.now();
        OffsetDateTime odt = OffsetDateTime.of(ldt, ZoneOffset.systemDefault().getRules().getOffset(ldt));

        Player player = ChatMessenger.player((org.bukkit.entity.Player)issuer.getIssuer());
        player.sendMessage(Component.text("1: " + formatter1.format(odt), NamedTextColor.GREEN));
        player.sendMessage(Component.text("2: " + formatter2.format(odt), NamedTextColor.GREEN));
    }

}