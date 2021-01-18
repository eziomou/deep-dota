package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Observable;

public interface FullPublicMatchRepository {

    Observable<FullPublicMatch> findAllAsc();

    Observable<FullPublicMatch> findAllAscAboveId(long matchId);

    Observable<FullPublicMatch> findAllDesc();

    Observable<FullPublicMatch> findAllDescBelowId(long matchId);
}
