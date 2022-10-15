package xyz.mauwh.featherchat.store;

import org.jetbrains.annotations.NotNull;
import xyz.mauwh.featherchat.exception.DataEntityAccessException;

public interface DataAccessObject<T, U> {
    void create(@NotNull T object) throws DataEntityAccessException;
    T read(@NotNull U identifier) throws DataEntityAccessException;
    void update(@NotNull T object) throws DataEntityAccessException;
    void delete(@NotNull T object) throws DataEntityAccessException;
}
