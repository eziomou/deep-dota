package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.eziomou.data.Player;
import io.github.eziomou.data.PlayerReadRepository;
import io.reactivex.rxjava3.core.Maybe;

public final class OpenDotaPlayerRepository implements PlayerReadRepository {

    private final OpenDota openDota = OpenDota.newInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlayerMapper playerMapper = new PlayerMapper();

    @Override
    public Maybe<Player> findOneByAccountIdAndHeroId(long accountId, int heroId) {
        return openDota.explorer("SELECT pm.account_id, pm.hero_id, count(pm.account_id) as matches_count, sum(((pm.player_slot & 128 = 0) = m.radiant_win)::int) as matches_won" +
                " FROM player_matches pm" +
                " INNER JOIN matches m ON pm.match_id = m.match_id" +
                " GROUP BY pm.account_id, pm.hero_id" +
                " LIMIT 1")
                .flatMapMaybe(this::map);
    }

    private Maybe<Player> map(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode rowsNode = rootNode.get("rows");
        if (rowsNode != null && rowsNode.get(0) != null) {
            return Maybe.just(playerMapper.map(rowsNode.get(0)));
        }
        return Maybe.empty();
    }
}
