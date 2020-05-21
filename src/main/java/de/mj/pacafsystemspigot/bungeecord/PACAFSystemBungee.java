package de.mj.pacafsystemspigot.bungeecord;

import de.mj.pacafsystemspigot.bungeecord.commands.ClanCommand;
import de.mj.pacafsystemspigot.bungeecord.commands.FriendCommand;
import de.mj.pacafsystemspigot.bungeecord.commands.PACAFBungeeCommand;
import de.mj.pacafsystemspigot.bungeecord.commands.PartyCommand;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigManager;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import de.mj.pacafsystemspigot.bungeecord.languages.LanguageManager;
import de.mj.pacafsystemspigot.bungeecord.managers.PlayerManager;
import de.mj.pacafsystemspigot.database.DatabaseManager;
import net.md_5.bungee.api.plugin.Plugin;

public class PACAFSystemBungee extends Plugin {

    private ConfigManager configmanager;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        configmanager = new ConfigManager(this);

        databaseManager = new DatabaseManager(this);

        languageManager = new LanguageManager(this);

        getProxy().getPluginManager().registerListener(this, new PlayerManager(this));
        getProxy().getPluginManager().registerCommand(this, new PACAFBungeeCommand(this, "pacafbungee"));

        if (ConfigValues.isFriendEnabled())
            getProxy().getPluginManager().registerCommand(this, new FriendCommand(this));
        if (ConfigValues.isClanEnabled())
            getProxy().getPluginManager().registerCommand(this, new ClanCommand());
        if (ConfigValues.isPartyEnabled())
            getProxy().getPluginManager().registerCommand(this, new PartyCommand());
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager getConfigManager() {
        return configmanager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
