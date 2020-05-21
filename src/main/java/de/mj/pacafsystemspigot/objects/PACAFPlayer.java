package de.mj.pacafsystemspigot.objects;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public interface PACAFPlayer {

    String getName();

    String getDisplayName();

    UUID getUUID();

    void sendFriendRequest(UUID receiverUUID);

    void removeFriend(UUID friendToBeRemoved);

    void acceptFriendRequest(UUID senderUUID);

    void denyFriendRequest(UUID senderUUID);

    List<UUID> getRequests();

    Set<UUID> getFriends();

    PACAFClan getClan();

    PACAFParty getParty();

    boolean isFriendOf(UUID uuid);

    Locale getLocale();

}
