package io.github.eziomou.deepdota.data.mongo;

import io.github.eziomou.data.PublicPlayerMatch;
import org.bson.Document;

final class PublicPlayerMatchMapper {

    public PublicPlayerMatch asPublicPlayerMatch(Document document) {
        return new PublicPlayerMatch(document.getLong("matchId"),
                document.getInteger("playerSlot"),
                document.getInteger("heroId"));
    }
}
