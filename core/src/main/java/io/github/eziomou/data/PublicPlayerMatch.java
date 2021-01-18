package io.github.eziomou.data;

import java.util.Objects;

public class PublicPlayerMatch {

    protected final long matchId;
    protected final int playerSlot;
    protected final int heroId;

    public PublicPlayerMatch(long matchId, int playerSlot, int heroId) {
        this.matchId = matchId;
        this.playerSlot = playerSlot;
        this.heroId = heroId;
    }

    public long getMatchId() {
        return matchId;
    }

    public int getPlayerSlot() {
        return playerSlot;
    }

    public int getHeroId() {
        return heroId;
    }

    public boolean isRadiant() {
        return (playerSlot & 128) == 0;
    }

    public int getPosition() {
        return playerSlot & 7;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PublicPlayerMatch that = (PublicPlayerMatch) object;
        return matchId == that.matchId &&
                playerSlot == that.playerSlot && heroId == that.heroId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, playerSlot, heroId);
    }

    @Override
    public String toString() {
        return "PublicPlayerMatch{" +
                "matchId=" + matchId +
                ", playerSlot=" + playerSlot +
                ", heroId=" + heroId +
                '}';
    }
}
