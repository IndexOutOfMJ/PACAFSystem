package de.mj.pacafsystemspigot.bungeecord.commands;

import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

public class ClanCommand extends Command {

    public ClanCommand() {
        super(ConfigValues.getClanMainCommand(), "", ConfigValues.getClanAlias());
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("Not implemented yet"));
    }
}
