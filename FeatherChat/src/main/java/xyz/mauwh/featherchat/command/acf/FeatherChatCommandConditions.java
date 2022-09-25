package xyz.mauwh.featherchat.command.acf;

import co.aikar.commands.*;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.channel.invite.ChannelInvitations;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.util.UUID;
import java.util.regex.Pattern;

public final class FeatherChatCommandConditions {

    private final Pattern channelNamePattern;

    public FeatherChatCommandConditions(@NotNull ChatMessengers<?, ?, ?> messengers, @NotNull ChannelInvitations invitations) {
        this.channelNamePattern = Pattern.compile("[0-9A-Za-z-_ ]+");
    }

    public <T extends CommandIssuer> void playerOnly(@NotNull ConditionContext<T> context) throws InvalidCommandArgument {
        if (!context.getIssuer().isPlayer()) {
            throw new ConditionFailedException("Only players may use this command");
        }
    }

    public <T extends CommandExecutionContext<T, U>, U extends CommandIssuer> void channelName(@NotNull ConditionContext<U> context, T cmdContext, @NotNull String name) throws InvalidCommandArgument {
        if (name.isBlank() || !channelNamePattern.matcher(name).matches()) {
            throw new ConditionFailedException("Invalid channel display name - may only contain letters, numbers, dashes, underscores, and spaces");
        }
    }

    public <T extends CommandExecutionContext<T, U>, U extends CommandIssuer> void charLimit(@NotNull ConditionContext<U> context, T cmdContext, @NotNull String value) throws InvalidCommandArgument {
        if (!context.hasConfig("max")) {
            return;
        }
        int maxLength = context.getConfigValue("max", 24);
        if (value.length() > maxLength) {
            throw new ConditionFailedException("Name must not be longer than " + maxLength + " characters");
        }
    }

}
