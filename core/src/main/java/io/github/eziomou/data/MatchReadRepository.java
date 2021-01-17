package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Observable;

public interface MatchReadRepository {

    Observable<Match> findAllAsc();

    Observable<Match> findAllAboveId(long matchId);
}
