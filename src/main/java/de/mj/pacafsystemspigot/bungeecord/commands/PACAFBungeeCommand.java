package de.mj.pacafsystemspigot.bungeecord.commands;

import de.mj.pacafsystemspigot.bungeecord.PACAFSystemBungee;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class PACAFBungeeCommand extends Command {

    private final PACAFSystemBungee plugin;

    public PACAFBungeeCommand(PACAFSystemBungee plugin, String name) {
        super(name, "pacaf.admin", "pacafb");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.getConfigManager().reloadConfigs())
            sender.sendMessage(new TextComponent(ConfigValues.getPrefix() + "§aSuccessfully reloaded all config files."));
        else
            sender.sendMessage(new TextComponent(ConfigValues.getPrefix() + "§cSomething went wrong! Have a look at your console to find the error."));
    }
}
