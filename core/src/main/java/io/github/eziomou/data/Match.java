package io.github.eziomou.data;

import java.util.ArrayList;
import java.util.List;

public final class Match extends MatchInfo {

    private final List<Player> radiant;
    private final List<Player> dire;

    public Match(long matchId, boolean radiantWin, List<Player> radiant, List<Player> dire) {
        super(matchId, radiantWin);
        this.radiant = radiant;
        this.dire = dire;
    }

    public List<Player> getRadiant() {
        return radiant;
    }

    public List<Player> getDire() {
        return dire;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(radiant);
        players.addAll(dire);
        return players;
    }
}
