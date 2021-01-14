package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Maybe;

public interface PlayerReadRepository {

    Maybe<Player> findOneByAccountIdAndHeroId(long accountId, int heroId);
}
