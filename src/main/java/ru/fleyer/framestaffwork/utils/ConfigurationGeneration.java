package ru.fleyer.framestaffwork.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigurationGeneration implements ConfigurationImpl {
    private FileConfiguration config;
    private FileConfiguration lang;
    private final String fileName;
    private final Plugin plugin;

    public ConfigurationGeneration(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.lang = yamlLoad();
        this.config = this.yamlLoad();
    }

    @Override
    public FileConfiguration yaml() {
        return this.config;
    }
    public FileConfiguration msg() {
        return this.lang;
    }

    @Override
    public FileConfiguration yamlLoad() {
        File file = new File(this.plugin.getDataFolder(), this.fileName);
        if (this.plugin.getResource(this.fileName) == null) {
            try {
                YamlConfiguration.loadConfiguration( file).save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return YamlConfiguration.loadConfiguration( file);
        }
        if (!file.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
        return YamlConfiguration.loadConfiguration( file);
    }

    @Override
    public void save() {
        try {
            this.config.save(this.fileName);
            this.lang.save(this.fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfiguration() {
        this.config = this.yamlLoad();
        this.lang = this.yamlLoad();
    }
}

