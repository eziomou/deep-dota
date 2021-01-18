package io.github.eziomou.data;

import java.util.List;

public class FullMatch extends FullPublicMatch {

    public FullMatch(long matchId, boolean radiantWin, List<? extends PlayerMatch> players) {
        super(matchId, radiantWin, players, true);
    }
}
