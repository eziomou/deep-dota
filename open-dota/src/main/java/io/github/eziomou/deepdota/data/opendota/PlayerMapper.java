package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.eziomou.data.Player;

final class PlayerMapper {

    public Player map(JsonNode node) {
        return Player.builder()
                .accountId(node.get("account_id").asLong())
                .heroId(node.get("hero_id").asInt())
                .totalMatches(node.get("matches_count").asInt())
                .wonMatches(node.get("matches_won").asInt())
                .build();
    }
}
