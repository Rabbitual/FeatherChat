package xyz.mauwh.featherchat.command.acf;

import co.aikar.commands.*;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.UUID;
import java.util.regex.Pattern;

public final class FeatherChatCommandConditions {

    private static final Pattern CHANNEL_NAME_PATTERN = Pattern.compile("[0-9A-Za-z-_ ]+");

    private FeatherChatCommandConditions() {
        throw new IllegalStateException("Unable to instantiate utility class " + getClass().getCanonicalName());
    }

    public static <T extends CommandIssuer> void playerOnly(@NotNull ConditionContext<T> context) throws InvalidCommandArgument {
        if (!context.getIssuer().isPlayer()) {
            throw new ConditionFailedException("Only players may use this command");
        }
    }

    public static <T extends CommandExecutionContext<T, V>, V extends CommandIssuer>
    void isMember(@NotNull ConditionContext<V> context, @NotNull T cmdContext, @NotNull UserChatChannel channel) throws InvalidCommandArgument {
        if (!channel.isMember(cmdContext.getIssuer().getUniqueId())) {
            throw new ConditionFailedException("Unable to find channel '" + channel.getName() + "'");
        }
    }

    public static <T extends CommandExecutionContext<T, U>, U extends CommandIssuer> void isOwner(@NotNull ConditionContext<U> context, T cmdContext, @NotNull UserChatChannel channel) throws InvalidCommandArgument {
        UUID issuerUUID = context.getIssuer().getUniqueId();
        if (issuerUUID.equals(channel.getOwner())) {
            return;
        } else if (!channel.isMember(issuerUUID)) {
            throw new ConditionFailedException("Unable to find channel '" + channel.getName() + "'");
        }
        throw new ConditionFailedException("You are not the owner of this channel");
    }

    public static <T extends CommandExecutionContext<T, U>, U extends CommandIssuer> void channelName(@NotNull ConditionContext<U> context, T cmdContext, @NotNull String name) throws InvalidCommandArgument {
        if (name.isBlank() || !CHANNEL_NAME_PATTERN.matcher(name).matches()) {
            throw new ConditionFailedException("Invalid channel display name - may only contain letters, numbers, dashes, underscores, and spaces");
        }
    }

    public static <T extends CommandExecutionContext<T, U>, U extends CommandIssuer> void charLimit(@NotNull ConditionContext<U> context, T cmdContext, @NotNull String value) throws InvalidCommandArgument {
        if (!context.hasConfig("max")) {
            return;
        }
        int maxLength = context.getConfigValue("max", 24);
        if (value.length() > maxLength) {
            throw new ConditionFailedException("Name must not be longer than " + maxLength + " characters");
        }
    }

}
