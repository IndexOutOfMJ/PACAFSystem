package de.mj.pacafsystemspigot.bungeecord.languages;

import com.google.common.collect.Maps;
import de.mj.pacafsystemspigot.bungeecord.PACAFSystemBungee;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import de.mj.pacafsystemspigot.objects.PACAFMessages;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;

public class LanguageManager {

    private final HashMap<String, PACAFMessages> loadedMessages = Maps.newHashMap();
    private final PACAFSystemBungee plugin;

    public LanguageManager(PACAFSystemBungee plugin) {
        this.plugin = plugin;
        try {
            createFiles();
            loadMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFiles() throws IOException {
        File langDir = new File(plugin.getDataFolder() + "/languages");
        if (!langDir.exists())
            langDir.mkdir();
        InputStream messagesInputSteam = getClass().getResourceAsStream("/languages/default.yml");
        InputStream germanMessagesInputStream = getClass().getResourceAsStream("/languages/german.yml");
        File messages = new File(plugin.getDataFolder() + "/languages", "default.yml");
        File germanMessages = new File(plugin.getDataFolder() + "/languages", "german.yml");
        if (!messages.exists())
            Files.copy(messagesInputSteam, messages.toPath());
        if (!germanMessages.exists())
            Files.copy(germanMessagesInputStream, germanMessages.toPath());
    }

    private void loadMessages() throws IOException {
        File languageDir = new File(plugin.getDataFolder() + "/languages");
        String[] filePaths = languageDir.list();
        for (int i = 0; i<filePaths.length; i++) {
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(ConfigValues.getPrefix() + "Language " + filePaths[i].replace(".yml", "") + " loaded"));
            File langFile = new File(plugin.getDataFolder() + "/languages", filePaths[i]);
            Configuration messageConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(langFile);
            PACAFMessages pacafMessages = new PACAFMessages() {
                @Override
                public String noPermission() {
                    return messageConfig.getString("noPermission");
                }

                @Override
                public String acceptButton() {
                    return messageConfig.getString("acceptButton");
                }

                @Override
                public String denyButton() {
                    return messageConfig.getString("denyButton");
                }

                @Override
                public String sendFriendRequest() {
                    return messageConfig.getString("sendFriendRequest");
                }

                @Override
                public String friendRequestAlreadySend() {
                    return messageConfig.getString("friendRequestAlreadySent");
                }

                @Override
                public String notAFriendOf() {
                    return messageConfig.getString("notAFriendOf");
                }

                @Override
                public String receivedFriendRequest() {
                    return messageConfig.getString("receivedFriendRequest");
                }

                @Override
                public String acceptFriendButtonHint() {
                    return messageConfig.getString("acceptFriendButtonHint");
                }

                @Override
                public String denyFriendButtonHint() {
                    return messageConfig.getString("denyFriendButtonHint");
                }

                @Override
                public String noFriendRequestReceived() {
                    return messageConfig.getString("noFriendRequestReceived");
                }

                @Override
                public String alreadyFriends() {
                    return messageConfig.getString("alreadyFriends");
                }

                @Override
                public String friendRemoved() {
                    return messageConfig.getString("friendRemoved");
                }

                @Override
                public String asFriendRemoved() {
                    return messageConfig.getString("asFriendRemoved");
                }

                @Override
                public String acceptedFriend() {
                    return messageConfig.getString("acceptedFriend");
                }

                @Override
                public String asFriendAccepted() {
                    return messageConfig.getString("asFriendAccepted");
                }

                @Override
                public String deniedFriend() {
                    return messageConfig.getString("deniedFriend");
                }

                @Override
                public String asFriendDenied() {
                    return messageConfig.getString("asFriendDenied");
                }

                @Override
                public String friendNowOnline() {
                    return messageConfig.getString("yourFriendIsNowOnline");
                }

                @Override
                public String unknownPlayer() {
                    return messageConfig.getString("unknownPlayer");
                }

                @Override
                public String errorMessage() {
                    return messageConfig.getString("errorMessage");
                }

                @Override
                public String commandCanOnlyExecutedByPlayers() {
                    return messageConfig.getString("commandCanOnlyExecutedByPlayers");
                }

                @Override
                public String friendIsNotOnline() {
                    return messageConfig.getString("friendIsNotOnline");
                }
            };
            loadedMessages.put(messageConfig.getString("locale"), pacafMessages);
        }
    }

    public PACAFMessages getMessages(@NotNull Locale locale) {
        if (loadedMessages.containsKey(locale.getLanguage()))
            return loadedMessages.get(locale.getLanguage());
        else
            return loadedMessages.get("default");
    }
}
