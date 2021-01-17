package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.FullMatch;
import io.github.eziomou.data.FullMatchRepository;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.*;

public final class MongoFullMatchRepository implements FullMatchRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoFullMatchRepository.class);

    private final MongoCollection<Document> matches;
    private final PlayerMatchMapper playerMatchMapper = new PlayerMatchMapper();

    public MongoFullMatchRepository(MongoDatabase database) {
        this.matches = database.getCollection("matches");
    }

    @Override
    public Observable<FullMatch> findAllAsc() {
        return Observable
                .fromPublisher(matches.aggregate(List.of(
                        lookup("playerMatches", "matchId", "matchId", "players"))))
                .map(this::asFullMatch);
    }

    @Override
    public Observable<FullMatch> findAllAscAboveId(long matchId) {
        return Observable
                .fromPublisher(matches.aggregate(List.of(
                        match(gt("matchId", matchId)),
                        sort(Sorts.ascending("matchId")),
                        lookup("playerMatches", "matchId", "matchId", "players"))))
                .map(this::asFullMatch);
    }

    @Override
    public Observable<FullMatch> findAllDesc() {
        return Observable
                .fromPublisher(matches.aggregate(List.of(
                        sort(Sorts.descending("matchId")),
                        lookup("playerMatches", "matchId", "matchId", "players"))))
                .map(this::asFullMatch);
    }

    @Override
    public Observable<FullMatch> findAllDescBelowId(long matchId) {
        return Observable
                .fromPublisher(matches.aggregate(List.of(
                        match(lte("matchId", matchId)),
                        sort(Sorts.descending("matchId")),
                        lookup("playerMatches", "matchId", "matchId", "players"))))
                .map(this::asFullMatch);
    }

    private FullMatch asFullMatch(Document document) {
        return new FullMatch(document.getLong("matchId"),
                document.getBoolean("radiantWin"),
                document.getList("players", Document.class).stream()
                        .map(playerMatchMapper::asPlayerMatch)
                        .collect(Collectors.toList()));
    }
}
