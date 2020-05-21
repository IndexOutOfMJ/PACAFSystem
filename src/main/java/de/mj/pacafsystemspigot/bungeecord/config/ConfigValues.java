package de.mj.pacafsystemspigot.bungeecord.config;

import de.mj.pacafsystemspigot.database.StorageMethod;

public class ConfigValues {

    private static String prefix;

    private static boolean friendEnabled;
    private static boolean clanEnabled;
    private static boolean partyEnabled;

    private static String friendMainCommand;
    private static String[] friendAlias;

    private static String clanMainCommand;
    private static String[] clanAlias;

    private static String partyMainCommand;
    private static String[] partyAlias;

    private static StorageMethod storageMethod;
    private static String address;
    private static String databaseName;
    private static String username;
    private static String password;

    private static int maxFriendsPerPlayer;
    private static int maxPlayersPerClan;
    private static int maxPlayersPerParty;

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        ConfigValues.prefix = prefix;
    }

    public static boolean isFriendEnabled() {
        return friendEnabled;
    }

    public static void setFriendEnabled(boolean friendEnabled) {
        ConfigValues.friendEnabled = friendEnabled;
    }

    public static boolean isClanEnabled() {
        return clanEnabled;
    }

    public static void setClanEnabled(boolean clanEnabled) {
        ConfigValues.clanEnabled = clanEnabled;
    }

    public static boolean isPartyEnabled() {
        return partyEnabled;
    }

    public static void setPartyEnabled(boolean partyEnabled) {
        ConfigValues.partyEnabled = partyEnabled;
    }

    public static String getFriendMainCommand() {
        return friendMainCommand;
    }

    public static void setFriendMainCommand(String friendMainCommand) {
        ConfigValues.friendMainCommand = friendMainCommand;
    }

    public static String[] getFriendAlias() {
        return friendAlias;
    }

    public static void setFriendAlias(String[] friendAlias) {
        ConfigValues.friendAlias = friendAlias;
    }

    public static String getClanMainCommand() {
        return clanMainCommand;
    }

    public static void setClanMainCommand(String clanMainCommand) {
        ConfigValues.clanMainCommand = clanMainCommand;
    }

    public static String[] getClanAlias() {
        return clanAlias;
    }

    public static void setClanAlias(String[] clanAlias) {
        ConfigValues.clanAlias = clanAlias;
    }

    public static String getPartyMainCommand() {
        return partyMainCommand;
    }

    public static void setPartyMainCommand(String partyMainCommand) {
        ConfigValues.partyMainCommand = partyMainCommand;
    }

    public static String[] getPartyAlias() {
        return partyAlias;
    }

    public static void setPartyAlias(String[] partyAlias) {
        ConfigValues.partyAlias = partyAlias;
    }

    public static StorageMethod getStorageMethod() {
        return storageMethod;
    }

    public static void setStorageMethod(StorageMethod storageMethod) {
        ConfigValues.storageMethod = storageMethod;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        ConfigValues.databaseName = databaseName;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        ConfigValues.address = address;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ConfigValues.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        ConfigValues.password = password;
    }

    public static int getMaxFriendsPerPlayer() {
        return maxFriendsPerPlayer;
    }

    public static void setMaxFriendsPerPlayer(int maxFriendsPerPlayer) {
        ConfigValues.maxFriendsPerPlayer = maxFriendsPerPlayer;
    }

    public static int getMaxPlayersPerClan() {
        return maxPlayersPerClan;
    }

    public static void setMaxPlayersPerClan(int maxPlayersPerClan) {
        ConfigValues.maxPlayersPerClan = maxPlayersPerClan;
    }

    public static int getMaxPlayersPerParty() {
        return maxPlayersPerParty;
    }

    public static void setMaxPlayersPerParty(int maxPlayersPerParty) {
        ConfigValues.maxPlayersPerParty = maxPlayersPerParty;
    }
}
