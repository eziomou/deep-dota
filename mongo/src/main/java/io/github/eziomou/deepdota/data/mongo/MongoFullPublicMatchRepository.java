package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.FullPublicMatch;
import io.github.eziomou.data.FullPublicMatchRepository;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;

public final class MongoFullPublicMatchRepository implements FullPublicMatchRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoFullPublicMatchRepository.class);

    private final MongoCollection<Document> publicMatches;
    private final PublicPlayerMatchMapper publicPlayerMatchMapper = new PublicPlayerMatchMapper();

    public MongoFullPublicMatchRepository(MongoDatabase database) {
        this.publicMatches = database.getCollection("publicMatches");
    }

    @Override
    public Observable<FullPublicMatch> findAllAsc() {
        return Observable
                .fromPublisher(publicMatches.aggregate(List.of(
                        lookupPublicPlayerMatches())))
                .map(this::asFullPublicMatch);
    }

    @Override
    public Observable<FullPublicMatch> findAllAscAboveId(long matchId) {
        return Observable
                .fromPublisher(publicMatches.aggregate(List.of(
                        match(gt("matchId", matchId)),
                        sort(Sorts.ascending("matchId")),
                        lookupPublicPlayerMatches())))
                .map(this::asFullPublicMatch);
    }

    @Override
    public Observable<FullPublicMatch> findAllDesc() {
        return Observable
                .fromPublisher(publicMatches.aggregate(List.of(
                        sort(Sorts.descending("matchId")),
                        lookupPublicPlayerMatches())))
                .map(this::asFullPublicMatch);
    }

    @Override
    public Observable<FullPublicMatch> findAllDescBelowId(long matchId) {
        return Observable
                .fromPublisher(publicMatches.aggregate(List.of(
                        match(lte("matchId", matchId)),
                        sort(Sorts.descending("matchId")),
                        lookupPublicPlayerMatches())))
                .map(this::asFullPublicMatch);
    }

    private Bson lookupPublicPlayerMatches() {
        return lookup("publicPlayerMatches", "matchId", "matchId", "players");
    }

    private FullPublicMatch asFullPublicMatch(Document document) {
        return new FullPublicMatch(document.getLong("matchId"),
                document.getBoolean("radiantWin"),
                document.getList("players", Document.class).stream()
                        .map(publicPlayerMatchMapper::asPublicPlayerMatch)
                        .collect(Collectors.toList()));
    }
}
