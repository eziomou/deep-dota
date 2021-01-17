package io.github.eziomou.deepdota.data.mongo;

import io.github.eziomou.data.PlayerMatch;
import org.bson.Document;

final class PlayerMatchMapper {

    public PlayerMatch asPlayerMatch(Document document) {
        return new PlayerMatch(document.getLong("matchId"),
                document.getLong("accountId"),
                document.getInteger("playerSlot"),
                document.getInteger("heroId"));
    }
}
