package xyz.mauwh.featherchat.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.message.ChannelMessage;
import xyz.mauwh.featherchat.api.channel.ChatChannel;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChannelMessageHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm", Locale.US);

    @NotNull
    public Component formatMessage(@NotNull ChannelMessage message) {
        String format = message.getChannel().getMessageFormat();
        return MiniMessage.miniMessage().deserialize(format, createMessageTags(message));
    }

    @NotNull
    public String formatPreview(@NotNull ChannelMessage message) {
        String format = message.getChannel().getMessageFormat();
        Component rawFormat = MiniMessage.miniMessage().deserialize(format, createPreviewTags(message));
        return LegacyComponentSerializer.legacySection().serialize(rawFormat);
    }

    @NotNull
    private TagResolver[] createMessageTags(@NotNull ChannelMessage message) {
        ChatChannel channel = message.getChannel();
        ChatMessenger sender = message.getSender();
        return new TagResolver[] {
            Placeholder.component("timestamp", Component.text(formatter.format(LocalDateTime.now()))),
            Placeholder.component("channel_name", channel.getFriendlyName()),
            Placeholder.component("sender_name", sender.getFriendlyName()),
            Placeholder.component("message", message.getMessage())
        };
    }

    @NotNull
    private TagResolver[] createPreviewTags(@NotNull ChannelMessage message) {
        ChatChannel channel = message.getChannel();
        return new TagResolver[] {
                Placeholder.component("timestamp", Component.text(formatter.format(LocalDateTime.now()))),
                Placeholder.component("channel_name", channel.getFriendlyName()),
                Placeholder.component("sender_name", Component.text("%1$s")),
                Placeholder.component("message", Component.text("%2$s"))
        };
    }

}
