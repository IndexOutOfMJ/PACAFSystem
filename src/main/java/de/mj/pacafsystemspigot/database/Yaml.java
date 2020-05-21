package de.mj.pacafsystemspigot.database;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Yaml {

    private final Plugin plugin;
    private File playerFile;
    private Configuration playerConfig;
    private File clanFile;
    private Configuration clanConfig;

    public Yaml(Plugin plugin) {
        this.plugin = plugin;
        try {
            createFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            playerConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(playerFile);
            clanConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(clanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFiles() throws IOException {
        playerFile = new File(plugin.getDataFolder() + "/storage", "players.yml");
        clanFile = new File(plugin.getDataFolder() + "/storage", "clans.yml");

        if (!playerFile.exists()) {
            playerFile.getParentFile().mkdir();
            playerFile.createNewFile();
        }
        if (!clanFile.exists()) {
            clanFile.getParentFile().mkdir();
            clanFile.createNewFile();
        }
    }

    public void set(@NotNull YAMLFile yamlFile, String key, Object value) throws IOException {
        if (yamlFile.equals(YAMLFile.PLAYER_FILE)) {
            playerConfig.set(key, value);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerConfig, new File(plugin.getDataFolder() + "/storage", "players.yml"));
            return;
        }
        if (yamlFile.equals(YAMLFile.CLAN_FILE)) {
            clanConfig.set(key, value);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(clanConfig, new File(plugin.getDataFolder() + "/storage", "clans.yml"));
        }
    }

    public Object get(@NotNull YAMLFile yamlFile, String key) {
        if (yamlFile.equals(YAMLFile.PLAYER_FILE)) {
            return playerConfig.get(key);
        }
        if (yamlFile.equals(YAMLFile.CLAN_FILE)) {
            return clanConfig.get(key);
        }
        return null;
    }

    public enum YAMLFile {
        PLAYER_FILE, CLAN_FILE
    }
}
