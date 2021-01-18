package io.github.eziomou.data;

import java.util.Objects;


public class PlayerMatch extends PublicPlayerMatch {

    private final long accountId;

    public PlayerMatch(long matchId, long accountId, int playerSlot, int heroId) {
        super(matchId, playerSlot, heroId);
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PlayerMatch that = (PlayerMatch) object;
        return matchId == that.matchId && accountId == that.accountId &&
                playerSlot == that.playerSlot && heroId == that.heroId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, accountId, playerSlot, heroId);
    }

    @Override
    public String toString() {
        return "PlayerMatch{" +
                "matchId=" + matchId +
                ", accountId=" + accountId +
                ", playerSlot=" + playerSlot +
                ", heroId=" + heroId +
                '}';
    }
}
