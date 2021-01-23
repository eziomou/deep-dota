package io.github.eziomou.data;

import java.util.List;

public class FullMatch extends FullPublicMatch {

    public FullMatch(long matchId, boolean radiantWin, int duration, int lobbyType, int gameMode, List<? extends PlayerMatch> players) {
        super(matchId, radiantWin, duration, lobbyType, gameMode, players, true);
    }
}
