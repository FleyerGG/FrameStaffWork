package ru.fleyer.framestaffwork.utils;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationImpl {
    FileConfiguration yaml();
    FileConfiguration msg();

    FileConfiguration yamlLoad();

    void save();

    void reloadConfiguration();
}
