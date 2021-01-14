package io.github.eziomou.data;

import java.util.Objects;

public class MatchInfo {

    private final long matchId;
    private final boolean radiantWin;

    public MatchInfo(long matchId, boolean radiantWin) {
        this.matchId = matchId;
        this.radiantWin = radiantWin;
    }

    public long getMatchId() {
        return matchId;
    }

    public boolean isRadiantWin() {
        return radiantWin;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MatchInfo matchInfo = (MatchInfo) object;
        return matchId == matchInfo.matchId && radiantWin == matchInfo.radiantWin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, radiantWin);
    }

    @Override
    public String toString() {
        return "MatchInfo{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                '}';
    }
}
