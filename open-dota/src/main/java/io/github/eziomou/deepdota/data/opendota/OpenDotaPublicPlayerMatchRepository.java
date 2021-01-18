package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.eziomou.data.PublicPlayerMatch;
import io.github.eziomou.data.PublicPlayerMatchReadRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

public final class OpenDotaPublicPlayerMatchRepository implements PublicPlayerMatchReadRepository {

    public static final int DEFAULT_LIMIT_PER_REQUEST = 100_000;

    private final OpenDota openDota = OpenDota.newInstance();

    private final int limitPerRequest;

    public OpenDotaPublicPlayerMatchRepository() {
        this.limitPerRequest = DEFAULT_LIMIT_PER_REQUEST;
    }

    public OpenDotaPublicPlayerMatchRepository(int limitPerRequest) {
        this.limitPerRequest = limitPerRequest;
    }

    @Override
    public Observable<PublicPlayerMatch> findAllAsc() {
        return findAllAscAboveId(0);
    }

    public Observable<PublicPlayerMatch> findAllAscAboveId(long matchId) {
        return openDota.explorer("SELECT match_id, player_slot, hero_id" +
                " FROM public_player_matches" +
                " WHERE match_id > " + matchId +
                " ORDER BY match_id ASC" +
                " LIMIT " + limitPerRequest, this::asPublicPlayerMatch)
                .toList()
                .flatMapObservable(players -> Observable.merge(Observable.fromIterable(players),
                                OpenDota.last(players).flatMapObservable(p -> findAllAscAboveId(p.getMatchId()))));
    }

    @Override
    public Observable<PublicPlayerMatch> findAllDesc() {
        return findAllDescBelowId(Long.MAX_VALUE);
    }

    @Override
    public Observable<PublicPlayerMatch> findAllDescBelowId(long matchId) {
        return openDota.explorer("SELECT match_id, player_slot, hero_id" +
                " FROM public_player_matches" +
                " WHERE match_id < " + matchId +
                " ORDER BY match_id DESC" +
                " LIMIT " + limitPerRequest, this::asPublicPlayerMatch)
                .toList()
                .flatMapObservable(players -> Observable.merge(Observable.fromIterable(players),
                        OpenDota.last(players).flatMapObservable(p -> findAllDescBelowId(p.getMatchId()))));
    }

    private PublicPlayerMatch asPublicPlayerMatch(JsonNode node) {
        return new PublicPlayerMatch(getId(node), node.get("player_slot").asInt(), node.get("hero_id").asInt());
    }

    private Long getId(JsonNode node) {
        return node.get("match_id").asLong();
    }

    @Override
    public Maybe<Long> findMaxId() {
        return openDota.explorer("SELECT match_id" +
                " FROM public_player_matches" +
                " ORDER BY match_id DESC" +
                " LIMIT 1", this::getId)
                .firstElement();
    }

    @Override
    public Maybe<Long> findMinId() {
        return openDota.explorer("SELECT match_id" +
                " FROM public_player_matches" +
                " ORDER BY match_id ASC" +
                " LIMIT 1", this::getId)
                .firstElement();
    }
}
