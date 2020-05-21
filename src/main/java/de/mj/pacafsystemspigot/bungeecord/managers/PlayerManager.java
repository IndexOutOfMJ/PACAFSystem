package de.mj.pacafsystemspigot.bungeecord.managers;

import de.mj.pacafsystemspigot.bungeecord.PACAFSystemBungee;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import de.mj.pacafsystemspigot.bungeecord.languages.LanguageManager;
import de.mj.pacafsystemspigot.objects.PACAFClan;
import de.mj.pacafsystemspigot.objects.PACAFParty;
import de.mj.pacafsystemspigot.objects.PACAFPlayer;
import de.mj.pacafsystemspigot.utils.UUIDFetcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerManager implements Listener {

    private final PACAFSystemBungee plugin;
    private static final Set<PACAFPlayer> pacafPlayers = new HashSet<>();
    private final UUIDFetcher uuidFetcher = new UUIDFetcher();
    private final LanguageManager languageManager;

    public PlayerManager(PACAFSystemBungee plugin) {
        this.plugin = plugin;
        this.languageManager = plugin.getLanguageManager();
    }

    @EventHandler
    public void onSettingsChanged(SettingsChangedEvent settingsChangedEvent) {
        ProxiedPlayer proxiedPlayer = settingsChangedEvent.getPlayer();
        plugin.getDatabaseManager().addPlayerToDatabase(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());

        PACAFPlayer pacafPlayer =
                new PACAFPlayer() {
                    @Override
                    public String getName() {
                        return proxiedPlayer.getName();
                    }

                    @Override
                    public String getDisplayName() {
                        return proxiedPlayer.getDisplayName();
                    }

                    @Override
                    public UUID getUUID() {
                        return proxiedPlayer.getUniqueId();
                    }

                    @Override
                    public void sendFriendRequest(UUID receiverUUID) {
                        if (proxiedPlayer.getUniqueId().equals(receiverUUID)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).errorMessage());
                            return;
                        }
                        if (!plugin.getDatabaseManager().exists(receiverUUID.toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).unknownPlayer());
                            return;
                        }
                        if (isFriendOf(receiverUUID)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).alreadyFriends(), receiverUUID);
                            return;
                        }
                        if (plugin.getDatabaseManager().hasFriendRequest(proxiedPlayer.getUniqueId().toString(), receiverUUID.toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).friendRequestAlreadySend(), receiverUUID);
                            return;
                        }
                        plugin.getDatabaseManager().sendFriendRequest(proxiedPlayer.getUniqueId().toString(), receiverUUID.toString());
                        sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).sendFriendRequest(), receiverUUID);
                        if (ProxyServer.getInstance().getPlayer(receiverUUID) != null) {
                            TextComponent acceptComponent = new TextComponent(languageManager.getMessages(proxiedPlayer.getLocale()).acceptButton());
                            TextComponent denyComponent = new TextComponent(languageManager.getMessages(proxiedPlayer.getLocale()).denyButton());
                            acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ConfigValues.getFriendMainCommand() + " accept"));
                            acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageManager.getMessages(proxiedPlayer.getLocale()).acceptFriendButtonHint()).create()));
                            denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ConfigValues.getFriendMainCommand() + " deny"));
                            denyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageManager.getMessages(proxiedPlayer.getLocale()).denyFriendButtonHint()).create()));
                            String message = ConfigValues.getPrefix() + languageManager.getMessages(proxiedPlayer.getLocale()).receivedFriendRequest().replace("{senderName}", proxiedPlayer.getName()).replace("{accept}", acceptComponent.toLegacyText()).replace("{deny}", denyComponent.toLegacyText());
                            sendMessage(ProxyServer.getInstance().getPlayer(receiverUUID), TextComponent.fromLegacyText(message));
                        }
                    }

                    @Override
                    public void removeFriend(UUID friendToBeRemoved) {
                        if (proxiedPlayer.getUniqueId().equals(friendToBeRemoved)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).errorMessage());
                            return;
                        }
                        if (!plugin.getDatabaseManager().exists(friendToBeRemoved.toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).unknownPlayer());
                            return;
                        }
                        if (!isFriendOf(friendToBeRemoved)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).notAFriendOf(), friendToBeRemoved);
                            return;
                        }
                        plugin.getDatabaseManager().removeFriend(proxiedPlayer.getUniqueId().toString(), friendToBeRemoved.toString());
                        sendMessage(proxiedPlayer, ConfigValues.getPrefix() + languageManager.getMessages(proxiedPlayer.getLocale()).friendRemoved(), friendToBeRemoved);
                        if (ProxyServer.getInstance().getPlayer(friendToBeRemoved) != null) {
                            sendMessage(ProxyServer.getInstance().getPlayer(friendToBeRemoved), languageManager.getMessages(proxiedPlayer.getLocale()).asFriendRemoved(), proxiedPlayer.getUniqueId());
                        }
                    }

                    @Override
                    public void acceptFriendRequest(UUID senderUUID) {
                        if (proxiedPlayer.getUniqueId().equals(senderUUID)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).errorMessage());
                            return;
                        }
                        if (!plugin.getDatabaseManager().exists(senderUUID.toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).unknownPlayer());
                            return;
                        }
                        if (isFriendOf(senderUUID)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).alreadyFriends(), senderUUID);
                            return;
                        }
                        if (!plugin.getDatabaseManager().hasFriendRequest(senderUUID.toString(), proxiedPlayer.getUniqueId().toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).noFriendRequestReceived(), senderUUID);
                            return;
                        }
                        plugin.getDatabaseManager().acceptFriendRequest(proxiedPlayer.getUniqueId().toString(), senderUUID.toString());
                        sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).acceptedFriend(), senderUUID);
                        if (ProxyServer.getInstance().getPlayer(senderUUID) != null) {
                            sendMessage(ProxyServer.getInstance().getPlayer(senderUUID), languageManager.getMessages(proxiedPlayer.getLocale()).asFriendAccepted(), proxiedPlayer.getUniqueId());
                        }
                    }

                    @Override
                    public void denyFriendRequest(UUID senderUUID) {
                        if (proxiedPlayer.getUniqueId().equals(senderUUID)) {
                            proxiedPlayer.sendMessage(new TextComponent(languageManager.getMessages(proxiedPlayer.getLocale()).errorMessage()));
                            return;
                        }
                        if (!plugin.getDatabaseManager().exists(senderUUID.toString())) {
                            proxiedPlayer.sendMessage(new TextComponent(languageManager.getMessages(proxiedPlayer.getLocale()).unknownPlayer()));
                            return;
                        }
                        if (isFriendOf(senderUUID)) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).alreadyFriends(), senderUUID);
                            return;
                        }
                        if (!plugin.getDatabaseManager().hasFriendRequest(senderUUID.toString(), proxiedPlayer.getUniqueId().toString())) {
                            sendMessage(proxiedPlayer, languageManager.getMessages(proxiedPlayer.getLocale()).noFriendRequestReceived(), senderUUID);
                            return;
                        }
                        plugin.getDatabaseManager().denyFriendRequest(proxiedPlayer.getUniqueId().toString(), senderUUID.toString());
                    }

                    @Override
                    public List<UUID> getRequests() {
                        return plugin.getDatabaseManager().getRequests(proxiedPlayer.getUniqueId().toString());
                    }

                    @Override
                    public Set<UUID> getFriends() {
                        return plugin.getDatabaseManager().getFriends(proxiedPlayer.getUniqueId().toString());
                    }

                    @Contract(pure = true)
                    @Override
                    public @Nullable PACAFClan getClan() {
                        //TODO get clan
                        return null;
                    }

                    @Contract(pure = true)
                    @Override
                    public @Nullable PACAFParty getParty() {
                        return null;
                    }

                    @Override
                    public boolean isFriendOf(UUID uuid) {
                        return plugin.getDatabaseManager().getFriends(proxiedPlayer.getUniqueId().toString()).contains(uuid);
                    }

                    @Override
                    public Locale getLocale() {
                        return proxiedPlayer.getLocale();
                    }
                };
        pacafPlayers.remove(pacafPlayer);
        pacafPlayers.add(pacafPlayer);
        pacafPlayer.getFriends().forEach(friend -> ProxyServer.getInstance().getPlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.getUniqueId().equals(friend)) {
                onlinePlayer.sendMessage(new TextComponent(languageManager.getMessages(onlinePlayer.getLocale()).friendNowOnline().replace("{onlineFriend}", proxiedPlayer.getName())));
            }
        }));
    }

    public static Set<PACAFPlayer> getPacafPlayers() {
        return pacafPlayers;
    }

    public static PACAFPlayer getPacafPlayer(String name) {
        AtomicReference<PACAFPlayer> playerReference = new AtomicReference<>();
        pacafPlayers.forEach(pacafPlayer -> {
            if (pacafPlayer.getName().equalsIgnoreCase(name))
                playerReference.set(pacafPlayer);
        });
        return playerReference.get();
    }

    public static PACAFPlayer getPacafPlayer(UUID uuid) {
        AtomicReference<PACAFPlayer> playerReference = new AtomicReference<>();
        pacafPlayers.forEach(pacafPlayer -> {
            if (pacafPlayer.getUUID().equals(uuid))
                playerReference.set(pacafPlayer);
        });
        return playerReference.get();
    }

    public void sendMessage(@NotNull ProxiedPlayer proxiedPlayer, String text) {
        proxiedPlayer.sendMessage(new TextComponent(ConfigValues.getPrefix() + text));
    }

    public void sendMessage(@NotNull ProxiedPlayer proxiedPlayer, @NotNull String text, UUID receiverUUID) {
        proxiedPlayer.sendMessage(new TextComponent(ConfigValues.getPrefix() + text.replace("{receiverName}", uuidFetcher.getName(receiverUUID)).replace("{senderName}", proxiedPlayer.getName())));
    }

    public void sendMessage(@NotNull ProxiedPlayer proxiedPlayer, BaseComponent[] baseComponent) {
        proxiedPlayer.sendMessage(baseComponent);
    }
}
