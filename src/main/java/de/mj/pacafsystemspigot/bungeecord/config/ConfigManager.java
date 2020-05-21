package de.mj.pacafsystemspigot.bungeecord.config;

import de.mj.pacafsystemspigot.database.StorageMethod;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;

public class ConfigManager {

    private final Plugin plugin;

    private Configuration configuration;

    public ConfigManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        try {
            createFiles();
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        loadValues();
    }

    public void createFiles() throws IOException {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        InputStream confInputStream = getClass().getResourceAsStream("/configs/bungeeConfig.yml");
        File conf = new File(plugin.getDataFolder(), "config.yml");
        if (!conf.exists())
            Files.copy(confInputStream, conf.toPath());
    }

    public void loadFiles() throws IOException {
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
    }

    public void loadValues() {
        ConfigValues.setPrefix(configuration.getString("prefix"));

        ConfigValues.setFriendEnabled(configuration.getBoolean("enableFriends"));
        ConfigValues.setClanEnabled(configuration.getBoolean("enableClans"));
        ConfigValues.setPartyEnabled(configuration.getBoolean("enableParty"));

        ConfigValues.setFriendMainCommand(configuration.getString("friendsMainCommand"));
        ConfigValues.setFriendAlias(configuration.getStringList("friendsCommandAlias").toArray(new String[0]));
        ConfigValues.setClanMainCommand(configuration.getString("clanMainCommand"));
        ConfigValues.setClanAlias(configuration.getStringList("clanCommandAlias").toArray(new String[0]));
        ConfigValues.setPartyMainCommand(configuration.getString("partyMainCommand"));
        ConfigValues.setPartyAlias(configuration.getStringList("partyCommandAlias").toArray(new String[0]));

        ConfigValues.setStorageMethod(StorageMethod.valueOf(configuration.getString("storage-method").toUpperCase()));
        ConfigValues.setAddress(configuration.getString("address"));
        ConfigValues.setDatabaseName("database-name");
        ConfigValues.setUsername(configuration.getString("username"));
        ConfigValues.setPassword(configuration.getString("password"));

        ConfigValues.setMaxFriendsPerPlayer(configuration.getInt("maxFriendsPerPlayer"));
        ConfigValues.setMaxPlayersPerClan(configuration.getInt("maxPlayersPerClan"));
        ConfigValues.setMaxPlayersPerParty(configuration.getInt("maxPlayersPerParty"));
    }

    public boolean reloadConfigs() {
        try {
            loadFiles();
            loadValues();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
