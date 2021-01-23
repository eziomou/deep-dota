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
        return findAllAscAboveId(0);
    }

    @Override
    public Observable<Match> findAllAscAboveId(long matchId) {
        return openDota.explorer("SELECT match_id, radiant_win, duration, lobby_type, game_mode" +
                " FROM matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " LIMIT " + limitPerRequest, this::asMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllAscAboveId(m.getMatchId()))));
    }

    private Match asMatch(JsonNode node) {
        return new Match(node.get("match_id").asLong(), node.get("radiant_win").asBoolean(),
                node.get("duration").asInt(), node.get("lobby_type").asInt(), node.get("game_mode").asInt());
    }
}
