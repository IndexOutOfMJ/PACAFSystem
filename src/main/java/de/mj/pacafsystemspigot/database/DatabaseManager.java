package de.mj.pacafsystemspigot.database;

import com.google.common.collect.Sets;
import com.mongodb.client.model.Filters;
import de.mj.pacafsystemspigot.bungeecord.config.ConfigValues;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bson.Document;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DatabaseManager {

    private final StorageMethod storageMethod;
    private MongoDB mongoDB;
    private Yaml yaml;
    private AsyncMySQL asyncMySql;

    public DatabaseManager(Plugin plugin) {
        this.storageMethod = ConfigValues.getStorageMethod();
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            if (ConfigValues.getUsername() == null || ConfigValues.getUsername().isEmpty())
                mongoDB = new MongoDB(ConfigValues.getDatabaseName(), ConfigValues.getAddress());
            else
                mongoDB = new MongoDB(ConfigValues.getDatabaseName(), ConfigValues.getAddress(), ConfigValues.getUsername(), ConfigValues.getPassword());
            return;
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            yaml = new Yaml(plugin);
            return;
        }
        if (storageMethod.equals(StorageMethod.MYSQL)) {
            String[] address = ConfigValues.getAddress().split(":");
            asyncMySql = new AsyncMySQL(plugin, address[0], Integer.parseInt(address[1]), ConfigValues.getUsername(), ConfigValues.getPassword(), ConfigValues.getDatabaseName());
        }
    }

    /**
     * Get all friends of a player
     * @param uuid the players uuid whose friends we want to get
     * @return a list with all friend uuid's
     */
    public Set<UUID> getFriends(String uuid) {
        Set<UUID> friends = Sets.newHashSet();
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first();
            assert document != null;
            document.getList("friends", String.class).forEach(friendUUID -> friends.add(UUID.fromString(friendUUID)));
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            List<String> friendUUIDs = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, uuid + ".friends");
            if (friendUUIDs != null) {
                friendUUIDs.forEach(fuuid -> friends.add(UUID.fromString(fuuid)));
            }
        }
        if (storageMethod.equals(StorageMethod.MYSQL)) {
            //TODO MySQL implementation
        }
        return friends;
    }

    /**
     * Add a player to the database
     * @param uuid the uuid of the player we want to add
     * @param name the name of the player we want to add
     */
    public void addPlayerToDatabase(String uuid, String name) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            if (mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first() == null
                    || mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first().isEmpty()) {
                Document document = new Document("uuid", uuid).append("name", name).append("friends", new ArrayList<String>()).append("clanTag", "").append("requests", new ArrayList<String>());
                mongoDB.getFriendsCollection().insertOne(document);
            }
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            try {
                if (yaml.get(Yaml.YAMLFile.PLAYER_FILE, uuid) == null) {
                    yaml.set(Yaml.YAMLFile.PLAYER_FILE, uuid + ".name", name);
                    yaml.set(Yaml.YAMLFile.PLAYER_FILE, uuid + ".friends", new ArrayList<String>());
                    yaml.set(Yaml.YAMLFile.PLAYER_FILE, uuid + ".clanTag", "");
                    yaml.set(Yaml.YAMLFile.PLAYER_FILE, uuid + ".requests", new ArrayList<String>());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (storageMethod.equals(StorageMethod.MYSQL)) {
            PreparedStatement preparedStatement = asyncMySql.prepare("INSERT INTO '" + ConfigValues.getDatabaseName() + "' (UUID, NAME, FRIENDS, CLANTAG, REQUESTS) " +
                    "SELECT '" + uuid + "', '" + name + "', '[]', '', '[]' FROM DUAL WHERE NOT EXISTS (SELECT '*' FROM '" + ConfigValues.getDatabaseName() + "' WHERE UUID = '" + uuid + "');");
            asyncMySql.update(preparedStatement);
        }
    }

    /**
     * A player sends a friend request to another player
     * @param senderUUID the player who sends the request
     * @param receiverUUID the player who receives the request
     */
    public void sendFriendRequest(String senderUUID, String receiverUUID) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", receiverUUID)).first();
            document.getList("requests", String.class).add(senderUUID);
            mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", receiverUUID), document);
            return;
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            ArrayList<String> requests = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, receiverUUID + ".requests");
            requests.add(senderUUID);
            try {
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, receiverUUID + ".requests", requests);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a player already has a friend request of another player
     * @param senderUUID The player that send the friend request
     * @param receiverUUID The player that should receive the friend request
     * @return if the receiver already has a friend request or not
     */
    public boolean hasFriendRequest(String senderUUID, String receiverUUID) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", receiverUUID)).first();
            if (document != null) {
                return document.getList("requests", String.class).contains(senderUUID);
            }
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            List<String> requests = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, receiverUUID + ".requests");
            return requests.contains(senderUUID);
        }
        return false;
    }

    /**
     * A player accepts a friend request
     * @param acceptUUID the player who accepted
     * @param senderUUID the player who send the request
     */
    public void acceptFriendRequest(String acceptUUID, String senderUUID) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", acceptUUID)).first();
            if (document != null) {
                document.getList("requests", String.class).remove(senderUUID);
                document.getList("friends", String.class).add(senderUUID);
                mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", acceptUUID), document);
            }
            document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", senderUUID)).first();
            if (document != null) {
                document.getList("friends", String.class).add(acceptUUID);
                mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", senderUUID), document);
            }
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            ArrayList<String> requests = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, acceptUUID + ".requests");
            ArrayList<String> friends = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, acceptUUID + ".friends");
            requests.remove(senderUUID);
            friends.add(senderUUID);
            try {
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, acceptUUID + ".requests", requests);
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, acceptUUID + ".friends", friends);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> oFriends = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, senderUUID + ".friends");
            oFriends.add(acceptUUID);
            try {
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, senderUUID + ".friends", friends);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A player denies a friend request
     * @param deniedUUID the player who denied
     * @param senderUUID the player who send the request
     */
    public void denyFriendRequest(String deniedUUID, String senderUUID) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", deniedUUID)).first();
            if (document != null) {
                document.getList("requests", String.class).remove(senderUUID);
                mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", deniedUUID), document);
            }
        }
    }

    /**
     * Get all friend requests of a player
     * @param uuid the uuid of that player
     * @return a list with uuid's that send a request to the player
     */
    public List<UUID> getRequests(String uuid) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first();
            List<UUID> uuids = new ArrayList<>();
            document.getList("requests", String.class).forEach(requesterUUID -> uuids.add(UUID.fromString(requesterUUID)));
            return uuids;
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            List<String> data = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, uuid + ".requests");
            List<UUID> uuids = new ArrayList<>();
            data.forEach(requesterUUID -> uuids.add(UUID.fromString(requesterUUID)));
            return uuids;
        }
        //TODO implement MySQL
        return new ArrayList<>();
    }

    /**
     * Remove a player from the friendList of a player
     * @param uuid the uuid of the player who removes another player
     * @param removedUUID the player that should be removed from the friend list
     */
    public void removeFriend(String uuid, String removedUUID) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            Document document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first();
            document.getList("friends", String.class).remove(removedUUID);
            mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", uuid), document);
            document = mongoDB.getFriendsCollection().find(Filters.eq("uuid", removedUUID)).first();
            document.getList("friends", String.class).remove(uuid);
            mongoDB.getFriendsCollection().findOneAndReplace(Filters.eq("uuid", removedUUID), document);
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            ArrayList<String> friends = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, uuid + ".friends");
            friends.remove(removedUUID);
            try {
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, uuid + ".friends", friends);
            } catch (IOException e) {
                e.printStackTrace();
            }
            friends = (ArrayList<String>) yaml.get(Yaml.YAMLFile.PLAYER_FILE, removedUUID + ".friends");
            friends.remove(uuid);
            try {
                yaml.set(Yaml.YAMLFile.PLAYER_FILE, removedUUID + ".friends", friends);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (storageMethod.equals(StorageMethod.MYSQL)) {
            //TODO implement mysql
        }
    }

    public boolean exists(String uuid) {
        if (storageMethod.equals(StorageMethod.MONGODB)) {
            return mongoDB.getFriendsCollection().find(Filters.eq("uuid", uuid)).first() != null;
        }
        if (storageMethod.equals(StorageMethod.YAML)) {
            return yaml.get(Yaml.YAMLFile.PLAYER_FILE, uuid) != null;
        }
        if (storageMethod.equals(StorageMethod.MYSQL)) {
            //TODO implement MySQL
            return false;
        }
        return false;
    }

}
