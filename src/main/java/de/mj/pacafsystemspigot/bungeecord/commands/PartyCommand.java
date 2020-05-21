package de.mj.pacafsystemspigot.bungeecord.commands;

import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

    public PartyCommand() {
        super(ConfigValues.getPartyMainCommand(), "", ConfigValues.getPartyAlias());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("Not implemented yet"));
    }
}
