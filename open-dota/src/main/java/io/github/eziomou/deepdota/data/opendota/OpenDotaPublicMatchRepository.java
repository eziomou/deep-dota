package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.eziomou.data.PublicMatch;
import io.github.eziomou.data.PublicMatchReadRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

public final class OpenDotaPublicMatchRepository implements PublicMatchReadRepository {

    public static final int DEFAULT_LIMIT_PER_REQUEST = 100_000;

    private final OpenDota openDota = OpenDota.newInstance();

    private final int limitPerRequest;

    public OpenDotaPublicMatchRepository() {
        this.limitPerRequest = DEFAULT_LIMIT_PER_REQUEST;
    }

    public OpenDotaPublicMatchRepository(int limitPerRequest) {
        this.limitPerRequest = limitPerRequest;
    }

    @Override
    public Observable<PublicMatch> findAllAsc() {
        return findAllAscAboveId(0);
    }

    @Override
    public Observable<PublicMatch> findAllAscAboveId(long matchId) {
        return openDota.explorer("SELECT match_id, radiant_win, duration, lobby_type, game_mode" +
                " FROM public_matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " LIMIT " + limitPerRequest, this::asPublicMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllAscAboveId(m.getMatchId()))));
    }

    @Override
    public Observable<PublicMatch> findAllDesc() {
        return findAllDescBelowId(Long.MAX_VALUE);
    }

    @Override
    public Observable<PublicMatch> findAllDescBelowId(long matchId) {
        return openDota.explorer("SELECT match_id, radiant_win, duration, lobby_type, game_mode" +
                " FROM public_matches" +
                " WHERE match_id < " + matchId +
                " ORDER BY match_id DESC" +
                " LIMIT " + limitPerRequest, this::asPublicMatch)
                .toList()
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        OpenDota.last(matches).flatMapObservable(m -> findAllDescBelowId(m.getMatchId()))));
    }

    private PublicMatch asPublicMatch(JsonNode node) {
        return new PublicMatch(getId(node), node.get("radiant_win").asBoolean(),
                node.get("duration").asInt(), node.get("lobby_type").asInt(), node.get("game_mode").asInt());
    }

    private Long getId(JsonNode node) {
        return node.get("match_id").asLong();
    }

    @Override
    public Maybe<Long> findMaxId() {
        return openDota.explorer("SELECT match_id" +
                " FROM public_matches" +
                " ORDER BY match_id DESC" +
                " LIMIT 1", this::getId)
                .firstElement();
    }

    @Override
    public Maybe<Long> findMinId() {
        return openDota.explorer("SELECT match_id" +
                " FROM public_matches" +
                " ORDER BY match_id ASC" +
                " LIMIT 1", this::getId)
                .firstElement();
    }
}
