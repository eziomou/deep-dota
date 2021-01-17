package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Observable;

public interface FullMatchRepository {

    Observable<FullMatch> findAllAsc();

    Observable<FullMatch> findAllAscAboveId(long matchId);

    Observable<FullMatch> findAllDesc();

    Observable<FullMatch> findAllDescBelowId(long matchId);
}
