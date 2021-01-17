package io.github.eziomou.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullMatch extends Match {

    private final List<PlayerMatch> players;

    public FullMatch(long matchId, boolean radiantWin, List<PlayerMatch> players) {
        this(matchId, radiantWin, players, true);
    }

    protected FullMatch(long matchId, boolean radiantWin, List<PlayerMatch> players, boolean copy) {
        super(matchId, radiantWin);
        this.players = copy ? new ArrayList<>(players) : players;
    }

    public List<PlayerMatch> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
