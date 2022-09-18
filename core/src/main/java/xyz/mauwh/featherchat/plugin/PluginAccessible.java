package xyz.mauwh.featherchat.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public interface PluginAccessible {
    @NotNull File getDataFolder();
    @NotNull Logger getLogger();
}
