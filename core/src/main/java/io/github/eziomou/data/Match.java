package io.github.eziomou.data;

import java.util.Objects;

public class Match {

    private final long matchId;
    private final boolean radiantWin;

    public Match(long matchId, boolean radiantWin) {
        this.matchId = matchId;
        this.radiantWin = radiantWin;
    }

    public long getMatchId() {
        return matchId;
    }

    public boolean isRadiantWin() {
        return radiantWin;
    }

    public boolean isWinner(PlayerMatch player) {
        return radiantWin == player.isRadiant();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Match other = (Match) object;
        return matchId == other.matchId && radiantWin == other.radiantWin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, radiantWin);
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
