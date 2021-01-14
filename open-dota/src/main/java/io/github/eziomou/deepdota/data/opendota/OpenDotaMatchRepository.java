package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.eziomou.data.*;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OpenDotaMatchRepository implements MatchReadRepository {

    public static final int DEFAULT_LIMIT_PER_REQUEST = 10_000 * 10;

    private final OpenDota openDota = OpenDota.newInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlayerMapper playerMapper = new PlayerMapper();

    public final int limitPerRequest;

    public OpenDotaMatchRepository() {
        this.limitPerRequest = DEFAULT_LIMIT_PER_REQUEST;
    }

    public OpenDotaMatchRepository(int limitPerRequest) {
        this.limitPerRequest = Math.max(10, limitPerRequest * 10);
    }

    @Override
    public Observable<Match> findAllDesc() {
        return findAll(null, "m.match_id DESC")
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        findAllBelowId(last(matches).getMatchId())));
    }

    @Override
    public Observable<Match> findAllBelowId(long matchId) {
        return findAll("m.match_id < " + matchId, "m.match_id DESC")
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        findAllBelowId(last(matches).getMatchId())));
    }

    @Override
    public Observable<Match> findAllAsc() {
        return findAll(null, "m.match_id ASC")
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        findAllAboveId(last(matches).getMatchId())));
    }

    @Override
    public Observable<Match> findAllAboveId(long matchId) {
        return findAll(null, "m.match_id ASC")
                .flatMapObservable(matches -> Observable.merge(Observable.fromIterable(matches),
                        findAllAboveId(last(matches).getMatchId())));
    }

    private Match last(List<? extends Match> matches) {
        return matches.get(matches.size() - 1);
    }

    private Maybe<List<Match>> findAll(String where, String order) {
        return openDota.explorer("SELECT m.match_id, m.radiant_win, pm.account_id, pm.player_slot, pm.hero_id, ph.matches_count, ph.matches_won" +
                " FROM matches m" +
                " LEFT JOIN (" +
                " SELECT pm.match_id, pm.account_id, pm.player_slot, pm.hero_id" +
                " FROM player_matches pm" +
                " WHERE match_id = pm.match_id" +
                ") AS pm ON m.match_id = pm.match_id" +
                " LEFT JOIN (" +
                " SELECT pm.account_id, pm.hero_id, count(pm.account_id) as matches_count, sum(((pm.player_slot & 128 = 0) = m.radiant_win)::int) as matches_won" +
                " FROM player_matches pm" +
                " INNER JOIN matches m ON pm.match_id = m.match_id" +
                " GROUP BY pm.account_id, pm.hero_id" +
                ") AS ph ON pm.account_id = ph.account_id AND pm.hero_id = ph.hero_id" +
                (where != null ? " WHERE " + where : "") +
                (order != null ? " ORDER BY " + order : "") +
                " LIMIT " + limitPerRequest)
                .map(this::map)
                .flatMapMaybe(matches -> matches.size() != 0 ? Maybe.just(matches) : Maybe.empty());
    }

    private List<Match> map(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode rowsNode = rootNode.get("rows");
        return asMatches(rowsNode);
    }

    private List<Match> asMatches(JsonNode rowsNode) {
        List<Match> matches = new ArrayList<>();

        Map<MatchInfo, List<Player>> radiant = new HashMap<>();
        Map<MatchInfo, List<Player>> dire = new HashMap<>();

        rowsNode.forEach(node -> {
            MatchInfo matchInfo = asMatchInfo(node);
            if (PlayerSlot.isRadiant(node.get("player_slot").asInt())) {
                add(radiant, matchInfo, playerMapper.map(node));
            } else {
                add(dire, matchInfo, playerMapper.map(node));
            }
            if (radiant.get(matchInfo) != null && radiant.get(matchInfo).size() == 5 &&
                    dire.get(matchInfo) != null && dire.get(matchInfo).size() == 5) {
                matches.add(new Match(matchInfo.getMatchId(), matchInfo.isRadiantWin(),
                        radiant.get(matchInfo), dire.get(matchInfo)));
            }
        });
        return matches;
    }

    private void add(Map<MatchInfo, List<Player>> team, MatchInfo matchInfo, Player player) {
        team.compute(matchInfo, (info, players) -> {
            if (players == null) {
                players = new ArrayList<>();
            }
            players.add(player);
            return players;
        });
    }

    private MatchInfo asMatchInfo(JsonNode node) {
        return new MatchInfo(node.get("match_id").asLong(), node.get("radiant_win").asBoolean());
    }
}
