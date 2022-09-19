package xyz.mauwh.featherchat.api.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

public final class ChannelMessage {

    private final ChatChannel channel;
    private final ChatMessenger<?> sender;
    private final Component message;

    public ChannelMessage(@NotNull ChatChannel channel, @NotNull ChatMessenger<?> sender, @NotNull Component message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    /**
     * Gets the recipient chat channel of this channel message.
     * @return the chat channel receiving this message
     */
    @NotNull
    public ChatChannel getChannel() {
        return channel;
    }

    /**
     * Gets the sender of this channel message.
     * @return the sender of this message
     */
    @NotNull
    public ChatMessenger<?> getSender() {
        return sender;
    }

    /**
     * The component-serialized input text received by the sender of this channel message.
     * @return the original message content that was input by the sender
     */
    @NotNull
    public Component getMessage() {
        return message;
    }

}
