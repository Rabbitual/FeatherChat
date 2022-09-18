package xyz.mauwh.featherchat.command;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.ConditionFailedException;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;

import java.util.UUID;
import java.util.regex.Pattern;

public interface FeatherChatBukkitCommandConditions {
    CommandConditions.Condition<BukkitCommandIssuer> PLAYER_ONLY = context -> {
        if (!context.getIssuer().isPlayer()) {
            throw new ConditionFailedException("Only players may use this command");
        }
    };

    FeatherChatParameterCondition<UserChatChannel> IS_MEMBER = (context, cmdContext, channel) -> {
        if (!channel.isMember(cmdContext.getIssuer().getUniqueId())) {
            throw new ConditionFailedException("Unable to find channel '" + channel.getName() + "'");
        }
    };

    FeatherChatParameterCondition<UserChatChannel> IS_OWNER = (context, cmdContext, channel) -> {
        UUID issuerUUID = context.getIssuer().getUniqueId();
        if (!channel.isMember(issuerUUID)) {
            throw new ConditionFailedException("Unable to find channel '" + channel.getName() + "'");
        } else if (issuerUUID.equals(channel.getOwner())) {
            throw new ConditionFailedException("You are not the owner of this channel");
        }
    };

    Pattern channelNamePattern = Pattern.compile("[0-9A-Za-z-_ ]+");
    FeatherChatParameterCondition<String> CHANNEL_NAME = (context, cmdContext, value) -> {
        if (value.isBlank() || !channelNamePattern.matcher(value).matches()) {
            throw new ConditionFailedException("Invalid channel display name - may only contain letters, numbers, dashes, underscores, and spaces");
        }
    };

    FeatherChatParameterCondition<String> CHAR_LIMIT = (context, cmdContext, value) -> {
        if (!context.hasConfig("max")) {
            return;
        }
        int maxLength = context.getConfigValue("max", 24);
        if (value.length() > maxLength) {
            throw new ConditionFailedException("Name must not be longer than " + maxLength + " characters");
        }
    };

    interface FeatherChatParameterCondition<P> extends CommandConditions.ParameterCondition<P, BukkitCommandExecutionContext, BukkitCommandIssuer> {}
}
