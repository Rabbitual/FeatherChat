package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.plugin.FeatherChatPlugin;

@CommandAlias("featherchat|fc")
public final class FeatherChatDefaultCommand extends BaseCommand {

    private final FeatherChatPlugin plugin;

    public FeatherChatDefaultCommand(@NotNull FeatherChatPlugin plugin) {
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
        plugin.getMessengers().getBySender(issuer.getIssuer()).setDisplayName(serialized);
    }

    @Subcommand("version")
    @CommandPermission("featherchat.version")
    public void onVersion(@NotNull CommandIssuer issuer) {
        issuer.sendMessage(String.format("FeatherChat v%s - authored by Mauwh", plugin.getVersion()));
    }

}
