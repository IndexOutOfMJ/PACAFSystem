package de.mj.pacafsystemspigot.spigot.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    private Path configPath;

    private File bungeeConfig;

    private File spigotConfig;

    private File messageConfig;

    private File inventoryConfig;

    public ConfigManager(String path, boolean isSpigot) {
        this.configPath = Paths.get(path);

        if (isSpigot)
            createSpigotConfigs();
        else
            createBungeeConfigs();
    }

    public void createSpigotConfigs() {

    }

    public void createBungeeConfigs() {

    }
}
