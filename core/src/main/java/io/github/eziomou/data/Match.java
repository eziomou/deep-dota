package io.github.eziomou.data;

public class Match extends PublicMatch {

    public Match(long matchId, boolean radiantWin) {
        super(matchId, radiantWin);
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
