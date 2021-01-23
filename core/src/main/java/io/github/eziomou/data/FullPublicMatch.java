package io.github.eziomou.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullPublicMatch extends PublicMatch {

    private final List<? extends PublicPlayerMatch> players;

    public FullPublicMatch(long matchId, boolean radiantWin, int duration, int lobbyType, int gameMode, List<? extends PublicPlayerMatch> players) {
        this(matchId, radiantWin, duration, lobbyType, gameMode, players, true);
    }

    protected FullPublicMatch(long matchId, boolean radiantWin, int duration, int lobbyType, int gameMode, List<? extends PublicPlayerMatch> players, boolean copy) {
        super(matchId, radiantWin, duration, lobbyType, gameMode);
        this.players = copy ? new ArrayList<>(players) : players;
    }

    public List<PublicPlayerMatch> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
