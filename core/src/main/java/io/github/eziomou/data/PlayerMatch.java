package io.github.eziomou.data;

import java.util.Objects;


public class PlayerMatch {

    private final long matchId;
    private final long accountId;
    private final int playerSlot;
    private final int heroId;

    public PlayerMatch(long matchId, long accountId, int playerSlot, int heroId) {
        this.matchId = matchId;
        this.accountId = accountId;
        this.playerSlot = playerSlot;
        this.heroId = heroId;
    }

    public long getMatchId() {
        return matchId;
    }

    public long getAccountId() {
        return accountId;
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
