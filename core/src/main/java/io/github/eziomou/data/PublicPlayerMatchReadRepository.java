package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

public interface PublicPlayerMatchReadRepository {

    Observable<PublicPlayerMatch> findAllAsc();

    Observable<PublicPlayerMatch> findAllAscAboveId(long matchId);

    Observable<PublicPlayerMatch> findAllDesc();

    Observable<PublicPlayerMatch> findAllDescBelowId(long matchId);

    Maybe<Long> findMaxId();

    Maybe<Long> findMinId();
}
