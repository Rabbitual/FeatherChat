package xyz.mauwh.featherchat.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.channel.UserChatChannel;
import xyz.mauwh.featherchat.api.message.ChannelMessage;
import xyz.mauwh.featherchat.api.messenger.ChatMessengers;
import xyz.mauwh.featherchat.api.messenger.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class ChannelMessageHandler {

    private final MiniMessage miniMessage;
    private final ChatMessengers<?> messengers;
    private final DateTimeFormatter formatter;

    public ChannelMessageHandler(@NotNull ChatMessengers<?> messengers) {
        this.miniMessage = MiniMessage.miniMessage();
        this.messengers = messengers;
        this.formatter = DateTimeFormatter.ofPattern("H:mm", Locale.US);
    }

    @NotNull
    public Component formatMessage(@NotNull ChannelMessage message) {
        String format = message.getChannel().getMessageFormat();
        return miniMessage.deserialize(format, createMessageTags(message, false));
    }

    @NotNull
    public String formatPreview(@NotNull ChannelMessage message) {
        String format = message.getChannel().getMessageFormat();
        Component rawFormat = miniMessage.deserialize(format, createMessageTags(message, true));
        return LegacyComponentSerializer.legacySection().serialize(rawFormat);
    }

    private final String[] infoMessages = new String[] {
            "<yellow><bold>| Channel Info",
            "<yellow><bold>| </bold> UUID:</yellow> <channel_uuid>",
            "<yellow><bold>| </bold> Name:</yellow> <channel_name>",
            "<yellow><bold>| </bold> Owner:</yellow> <channel_owner>",
            "<yellow><bold>| </bold> Members:</yellow> <channel_members>"
    };

    @NotNull
    public Component[] formatInfo(@NotNull UserChatChannel channel) {
        TagResolver[] infoTags = createInfoTags(channel);
        Component[] components = new Component[infoMessages.length];
        for (int i = 0; i < infoMessages.length; i++) {
            components[i] = miniMessage.deserialize(infoMessages[i], infoTags);
        }
        return components;
    }

    @NotNull
    private TagResolver[] createMessageTags(@NotNull ChannelMessage message, boolean preview) {
        Component channelName = message.getChannel().getFriendlyName();
        return new TagResolver[] {
            Placeholder.component("timestamp", text(formatter.format(LocalDateTime.now()))),
            Placeholder.component("channel_name", channelName),
            Placeholder.component("sender_name", preview ? text("%1$s") : message.getSender().getDisplayName()),
            Placeholder.component("message", preview ? text("%2$s") : message.getMessage())
        };
    }

    @NotNull
    private TagResolver[] createInfoTags(@NotNull UserChatChannel channel) {
        Component displayName = channel.getDisplayName()
                .map(dn -> dn.append(text(" (" + channel.getName() + ")")))
                .orElse(channel.getFriendlyName());

        TextComponent.Builder members = Component.text();
        Set<UUID> uuids = channel.getMembers();
        Iterator<UUID> iter = uuids.iterator();
        while (iter.hasNext()) {
            Player member = messengers.getByUUID(iter.next());
            members.append(member.getDisplayName());
            if (iter.hasNext()) {
                members.append(text(", "));
            }
        }

        return new TagResolver[] {
                Placeholder.component("channel_uuid", text(channel.getUUID().toString())),
                Placeholder.component("channel_name", displayName),
                Placeholder.component("channel_owner", messengers.getByUUID(channel.getOwner()).getDisplayName()),
                Placeholder.component("channel_members", members.build()),
        };
    }

}
