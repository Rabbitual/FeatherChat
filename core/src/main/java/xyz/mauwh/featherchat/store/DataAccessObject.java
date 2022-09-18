package xyz.mauwh.featherchat.store;

import org.jetbrains.annotations.NotNull;

public interface DataAccessObject<T, U> {
    void create(@NotNull T object);
    T read(@NotNull U identifier);
    void update(@NotNull T object);
    void delete(@NotNull T object);
}
