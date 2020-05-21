package de.mj.pacafsystemspigot.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDB {

    private final MongoCollection<Document> friendsCollection;
    private final MongoCollection<Document> clansCollection;

    public MongoDB(String databaseName, String address) {
        MongoClientURI uri = new MongoClientURI("mongodb://" + address);
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        friendsCollection = database.getCollection("pacafSystem_friends");
        clansCollection = database.getCollection("pacafSystem_clans");
    }

    public MongoDB(String prefix, String address, String username, String password) {
        MongoClientURI uri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + address);
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(prefix);
        friendsCollection = database.getCollection("pacafSystem_friends");
        clansCollection = database.getCollection("pacafSystem_clans");
    }

    public MongoCollection<Document> getFriendsCollection() {
        return friendsCollection;
    }

    public MongoCollection<Document> getClansCollection() {
        return clansCollection;
    }
}
