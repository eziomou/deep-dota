package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Observable;

public interface MatchReadRepository {

    Observable<Match> findAllDesc();

    Observable<Match> findAllBelowId(long matchId);

    Observable<Match> findAllAsc();

    Observable<Match> findAllAboveId(long matchId);
}
