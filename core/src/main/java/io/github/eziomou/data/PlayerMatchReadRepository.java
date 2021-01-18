package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Observable;

public interface PlayerMatchReadRepository {

    Observable<PlayerMatch> findAllAsc();

    Observable<PlayerMatch> findAllAscAboveId(long matchId);
}
