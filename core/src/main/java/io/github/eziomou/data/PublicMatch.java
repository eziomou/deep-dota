package io.github.eziomou.data;

import java.util.Objects;

public class PublicMatch {

    protected final long matchId;
    protected final boolean radiantWin;

    public PublicMatch(long matchId, boolean radiantWin) {
        this.matchId = matchId;
        this.radiantWin = radiantWin;
    }

    public long getMatchId() {
        return matchId;
    }

    public boolean isRadiantWin() {
        return radiantWin;
    }

    public boolean isWinner(PublicPlayerMatch player) {
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
        PublicMatch other = (PublicMatch) object;
        return matchId == other.matchId && radiantWin == other.radiantWin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, radiantWin);
    }

    @Override
    public String toString() {
        return "PublicMatch{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
