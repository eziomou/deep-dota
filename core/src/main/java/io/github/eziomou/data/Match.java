package io.github.eziomou.data;

public class Match extends PublicMatch {

    public Match(long matchId, boolean radiantWin, int duration, int lobbyType, int gameMode) {
        super(matchId, radiantWin, duration, lobbyType, gameMode);
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
