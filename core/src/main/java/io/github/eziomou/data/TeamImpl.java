package io.github.eziomou.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class TeamImpl implements Team {

    private final List<Player> players;

    TeamImpl(List<Player> players) {
        this(players, true);
    }

    TeamImpl(List<Player> players, boolean copy) {
        this.players = copy ? new ArrayList<>(players) : players;
    }

    @Override
    public Iterator<Player> iterator() {
        return players.iterator();
    }
}
