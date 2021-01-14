package io.github.eziomou.data;

import java.util.Objects;

public final class Player {

    private final long accountId;
    private final int heroId;
    private final int totalMatches;
    private final int wonMatches;

    private Player(Builder builder) {
        this.accountId = builder.accountId;
        this.heroId = builder.heroId;
        this.totalMatches = builder.totalMatches;
        this.wonMatches = builder.wonMatches;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getHeroId() {
        return heroId;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getWonMatches() {
        return wonMatches;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Player other = (Player) object;
        return accountId == other.accountId && heroId == other.heroId && totalMatches == other.totalMatches && wonMatches == other.wonMatches;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, heroId, totalMatches, wonMatches);
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "accountId=" + accountId +
                ", heroId=" + heroId +
                ", totalMatches=" + totalMatches +
                ", wonMatches=" + wonMatches +
                '}';
    }

    public static Builder create(long accountId) {
        return builder().accountId(accountId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long accountId;
        private int heroId;
        private int totalMatches;
        private int wonMatches;

        private Builder() {
        }

        public Builder accountId(long accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder hero(Hero hero) {
            return heroId(hero.getId());
        }

        public Builder heroId(int heroId) {
            this.heroId = heroId;
            return this;
        }

        public Builder totalMatches(int totalMatches) {
            this.totalMatches = totalMatches;
            return this;
        }

        public Builder wonMatches(int wonMatches) {
            this.wonMatches = wonMatches;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
