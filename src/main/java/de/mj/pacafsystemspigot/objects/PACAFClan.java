package de.mj.pacafsystemspigot.objects;

import java.util.Set;

public interface PACAFClan {

    String getTag();

    String getName();

    Set<PACAFPlayer> getPlayers();

    void addPlayer();

    void removePlayer();
}
