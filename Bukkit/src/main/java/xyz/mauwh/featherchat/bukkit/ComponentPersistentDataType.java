package xyz.mauwh.featherchat.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class ComponentPersistentDataType implements PersistentDataType<String, Component> {

    private static final ComponentPersistentDataType INSTANCE = new ComponentPersistentDataType();

    @NotNull
    public static ComponentPersistentDataType get() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<Component> getComplexType() {
        return Component.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull Component complex, @NotNull PersistentDataAdapterContext context) {
        return LegacyComponentSerializer.legacySection().serialize(complex);
    }

    @NotNull
    @Override
    public Component fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return LegacyComponentSerializer.legacySection().deserialize(primitive);
    }

}
