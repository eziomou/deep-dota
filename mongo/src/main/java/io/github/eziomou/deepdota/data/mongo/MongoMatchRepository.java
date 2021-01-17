package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.Match;
import io.github.eziomou.data.MatchReadRepository;
import io.github.eziomou.data.MatchWriteRepository;
import io.github.eziomou.data.Synchronizer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.gt;

public final class MongoMatchRepository implements MatchWriteRepository, MatchReadRepository,
        Synchronizer<MatchReadRepository> {

    private final MongoCollection<Document> matches;

    public MongoMatchRepository(MongoDatabase database) {
        this.matches = database.getCollection("matches");
    }

    @Override
    public Completable saveAll(List<? extends Match> matches) {
        return Completable.fromPublisher(this.matches.insertMany(matches.stream()
                .map(this::asDocument)
                .collect(Collectors.toList())));
    }

    private Document asDocument(Match match) {
        Document document = new Document();
        document.put("matchId", match.getMatchId());
        document.put("radiantWin", match.isRadiantWin());
        return document;
    }

    @Override
    public Observable<Match> findAllAsc() {
        return Observable.fromPublisher(matches.find()
                .sort(Sorts.ascending("matchId")))
                .map(this::asMatch);
    }

    @Override
    public Observable<Match> findAllAboveId(long matchId) {
        return Observable.fromPublisher(matches.find(gt("matchId", matchId))
                .sort(Sorts.ascending("matchId")))
                .map(this::asMatch);
    }

    private Match asMatch(Document document) {
        return new Match(document.getLong("matchId"),
                document.getBoolean("radiantWin"));
    }

    @Override
    public Completable synchronize(MatchReadRepository source) {
        return source.findAllAsc().toList().concatMapCompletable(this::saveAll);
    }
}
