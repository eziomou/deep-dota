package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.eziomou.data.PlayerMatch;
import io.github.eziomou.data.PlayerMatchReadRepository;
import io.reactivex.rxjava3.core.Observable;

public final class OpenDotaPlayerMatchRepository implements PlayerMatchReadRepository {

    public static final int DEFAULT_LIMIT_PER_REQUEST = 100_000;

    private final OpenDota openDota = OpenDota.newInstance();

    private final int limitPerRequest;

    public OpenDotaPlayerMatchRepository() {
        this.limitPerRequest = DEFAULT_LIMIT_PER_REQUEST;
    }

    public OpenDotaPlayerMatchRepository(int limitPerRequest) {
        this.limitPerRequest = limitPerRequest;
    }

    @Override
    public Observable<PlayerMatch> findAllAsc() {
        return findAllAscAboveId(0);
    }

    @Override
    public Observable<PlayerMatch> findAllAscAboveId(long matchId) {
        return openDota.explorer("SELECT match_id, account_id, player_slot, hero_id" +
                " FROM player_matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " LIMIT " + limitPerRequest, this::asPlayerMatch)
                .toList()
                .flatMapObservable(players -> Observable.merge(Observable.fromIterable(players),
                                OpenDota.last(players).flatMapObservable(p -> findAllAscAboveId(p.getMatchId()))));
    }

    private PlayerMatch asPlayerMatch(JsonNode node) {
        return new PlayerMatch(node.get("match_id").asLong(), node.get("account_id").asLong(),
                node.get("player_slot").asInt(), node.get("hero_id").asInt());
    }
}
