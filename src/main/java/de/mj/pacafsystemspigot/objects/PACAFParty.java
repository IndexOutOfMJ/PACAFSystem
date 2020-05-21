package de.mj.pacafsystemspigot.objects;

import java.util.Set;

public interface PACAFParty {

    PACAFPlayer getLeader();

    Set<PACAFPlayer> getPlayers();
}
