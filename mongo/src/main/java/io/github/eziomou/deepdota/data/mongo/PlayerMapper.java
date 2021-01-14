package io.github.eziomou.deepdota.data.mongo;

import io.github.eziomou.data.Player;
import org.bson.Document;

final class PlayerMapper {

    public Document mapToDocument(Player player) {
        Document document = new Document();
        document.put("accountId", player.getAccountId());
        document.put("heroId", player.getHeroId());
        document.put("totalMatches", player.getTotalMatches());
        document.put("wonMatches", player.getWonMatches());
        return document;
    }

    public Player mapToPlayer(Document document) {
        return Player.builder()
                .accountId(document.getLong("accountId"))
                .heroId(document.getInteger("heroId"))
                .totalMatches(document.getInteger("totalMatches"))
                .wonMatches(document.getInteger("wonMatches"))
                .build();
    }
}
