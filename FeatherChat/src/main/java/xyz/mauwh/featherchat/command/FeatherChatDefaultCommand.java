package xyz.mauwh.featherchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;
import xyz.mauwh.featherchat.api.messenger.Player;
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
    public void onHelp(@NotNull ChatMessenger issuer) {
        issuer.sendMessage(Component.text("WIP command", NamedTextColor.RED));
    }

    @Subcommand("displayname")
    @CommandAlias("nickname|nick")
    @Conditions("playerOnly")
    @CommandPermission("featherchat.displayname")
    public void onDisplayName(@NotNull Player issuer, @NotNull String displayName) {
        displayName = displayName.replaceAll("&[K-Ok-o]", "");
        Component serialized = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        issuer.setDisplayName(serialized);
        issuer.sendMessage(Component.text("Set display name to ", NamedTextColor.GREEN).append(serialized));
    }

    @Subcommand("version")
    @CommandPermission("featherchat.version")
    public void onVersion(@NotNull ChatMessenger issuer) {
        issuer.sendMessage(Component.text("FeatherChat v" + plugin.getVersion() + " - authored by Mauwh", NamedTextColor.YELLOW));
    }

}
