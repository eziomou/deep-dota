package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.eziomou.data.Match;
import io.github.eziomou.data.MatchReadRepository;
import io.reactivex.rxjava3.core.Observable;

public final class OpenDotaMatchRepository implements MatchReadRepository {

    public static final int DEFAULT_LIMIT_PER_REQUEST = 100_000;

    private final OpenDota openDota = OpenDota.newInstance();

    private final int limitPerRequest;

    public OpenDotaMatchRepository() {
        this.limitPerRequest = DEFAULT_LIMIT_PER_REQUEST;
    }

    public OpenDotaMatchRepository(int limitPerRequest) {
        this.limitPerRequest = limitPerRequest;
    }

    @Override
    public Observable<Match> findAllAsc() {
        return findAllAboveId(0);
    }

    public Observable<Match> findAllAsc(int offset) {
        return openDota.explorer("SELECT match_id, radiant_win" +
                " FROM matches" +
                " ORDER BY match_id ASC" +
                " OFFSET " + offset +
                " LIMIT " + limitPerRequest, this::asMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllAboveId(m.getMatchId()))));
    }

    @Override
    public Observable<Match> findAllAboveId(long matchId) {
        return openDota.explorer("SELECT match_id, radiant_win" +
                " FROM matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " LIMIT " + limitPerRequest, this::asMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllAboveId(m.getMatchId()))));
    }

    public Observable<Match> findAllAboveId(long matchId, int offset) {
        return openDota.explorer("SELECT match_id, radiant_win" +
                " FROM matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " OFFSET " + offset +
                " LIMIT " + limitPerRequest, this::asMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllAboveId(m.getMatchId()))));
    }


    private Match asMatch(JsonNode node) {
        return new Match(node.get("match_id").asLong(), node.get("radiant_win").asBoolean());
    }
}
