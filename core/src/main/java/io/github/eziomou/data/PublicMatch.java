package io.github.eziomou.data;

import java.util.Objects;

public class PublicMatch {

    protected final long matchId;
    protected final boolean radiantWin;
    private final int duration;
    private final int lobbyType;
    private final int gameMode;

    public PublicMatch(long matchId, boolean radiantWin, int duration, int lobbyType, int gameMode) {
        this.matchId = matchId;
        this.radiantWin = radiantWin;
        this.duration = duration;
        this.lobbyType = lobbyType;
        this.gameMode = gameMode;
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

    public int getDuration() {
        return duration;
    }

    public int getLobbyType() {
        return lobbyType;
    }

    public int getGameMode() {
        return gameMode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PublicMatch that = (PublicMatch) object;
        return matchId == that.matchId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId);
    }

    @Override
    public String toString() {
        return "PublicMatch{" +
                "matchId=" + matchId +
                ", radiantWin=" + radiantWin +
                ", duration=" + duration +
                ", lobbyType=" + lobbyType +
                ", gameMode=" + gameMode +
                '}';
    }
}
