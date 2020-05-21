package de.mj.pacafsystemspigot.bungeecord.commands;

import de.mj.pacafsystemspigot.bungeecord.PACAFSystemBungee;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import de.mj.pacafsystemspigot.bungeecord.languages.LanguageManager;
import de.mj.pacafsystemspigot.bungeecord.managers.PlayerManager;
import de.mj.pacafsystemspigot.objects.PACAFPlayer;
import de.mj.pacafsystemspigot.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FriendCommand extends Command implements TabExecutor {

    UUIDFetcher uuidFetcher = new UUIDFetcher();
    private final LanguageManager languageManager;

    public FriendCommand(PACAFSystemBungee plugin) {
        super(ConfigValues.getFriendMainCommand(), "", ConfigValues.getFriendAlias());
        languageManager = plugin.getLanguageManager();
    }

    //friend add, remove, msg, accept, deny, requests

    //TODO own args

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ConfigValues.getPrefix() + "This command can only executed by players!"));
            return;
        }

        PACAFPlayer pacafPlayer = PlayerManager.getPacafPlayer(((ProxiedPlayer) sender).getUniqueId());
        //requests
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("requests")) {
                List<UUID> requests = pacafPlayer.getRequests();
                if (requests.isEmpty()) {
                    sender.sendMessage(new TextComponent(ConfigValues.getPrefix() + "You have no friend requests!"));
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder(ConfigValues.getPrefix() + "Pending Requests:\n");
                requests.forEach(uuid -> stringBuilder.append(uuidFetcher.getName(uuid)).append("\n"));
                sender.sendMessage(new TextComponent(stringBuilder.toString()));
            }
        }

        //add, remove, accept, deny
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                pacafPlayer.sendFriendRequest(uuidFetcher.getUUID(args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                pacafPlayer.removeFriend(uuidFetcher.getUUID(args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                pacafPlayer.acceptFriendRequest(uuidFetcher.getUUID(args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                pacafPlayer.denyFriendRequest(uuidFetcher.getUUID(args[1]));
            }
        }

        //msg
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("msg")) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if (proxiedPlayer == null) {
                    sender.sendMessage(new TextComponent(ConfigValues.getPrefix() + languageManager.getMessages(((ProxiedPlayer) sender).getLocale()).friendIsNotOnline()));
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder("[" + proxiedPlayer.getName() + " -> me] ");
                for (int i = 2; i <= args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                proxiedPlayer.sendMessage(new TextComponent(ConfigValues.getPrefix() + stringBuilder.toString()));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(ConfigValues.getPrefix() + languageManager.getMessages(new Locale("default")).commandCanOnlyExecutedByPlayers()));
            return null;
        }
        if (strings[0].startsWith("f")) {

        }
        return null;
    }
}
