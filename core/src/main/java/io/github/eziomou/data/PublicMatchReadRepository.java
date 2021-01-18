package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

public interface PublicMatchReadRepository {

    Observable<PublicMatch> findAllAsc();

    Observable<PublicMatch> findAllAscAboveId(long matchId);

    Observable<PublicMatch> findAllDesc();

    Observable<PublicMatch> findAllDescBelowId(long matchId);

    Maybe<Long> findMaxId();

    Maybe<Long> findMinId();
}
