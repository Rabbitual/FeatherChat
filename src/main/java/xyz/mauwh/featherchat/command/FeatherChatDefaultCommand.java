package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.FeatherChat;
import xyz.mauwh.featherchat.messenger.ChatMessenger;

@CommandAlias("featherchat|fc")
public final class FeatherChatDefaultCommand extends BaseCommand {

    private final FeatherChat plugin;

    public FeatherChatDefaultCommand(@NotNull FeatherChat plugin) {
        this.plugin = plugin;
    }

    @HelpCommand
    @Subcommand("help")
    @CommandPermission("featherchat.help")
    public void onHelp(@NotNull CommandIssuer issuer) {
        issuer.sendMessage("WIP command");
    }

    @Subcommand("displayname")
    @CommandAlias("nickname|nick")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.displayname")
    public void onDisplayName(@NotNull CommandIssuer issuer, @NotNull String displayName) {
        displayName = displayName.replaceAll("&[K-Ok-o]", "");
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        ChatMessenger.player((Player)issuer.getIssuer()).setDisplayName(serialized);
    }

    @Subcommand("version")
    @CommandPermission("featherchat.version")
    public void onVersion(@NotNull CommandIssuer issuer) {
        plugin.getAudienceProvider().sender(issuer.getIssuer()).sendMessage(Component.text(
                String.format("FeatherChat v%s - authored by Mauwh", plugin.getDescription().getVersion()), NamedTextColor.YELLOW
        ));
    }

}
