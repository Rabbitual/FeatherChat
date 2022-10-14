package xyz.mauwh.featherchat.messenger;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mauwh.featherchat.api.messenger.ChatMessenger;

public abstract class AbstractChatMessenger implements ChatMessenger {

    protected final String name;
    protected Component displayName;

    public AbstractChatMessenger(@NotNull String name) {
        this.name = name;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return hasDisplayName() ? displayName : Component.text(name);
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    public boolean hasDisplayName() {
        return displayName != null;
    }

}
