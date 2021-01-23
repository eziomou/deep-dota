package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.PublicMatch;
import io.github.eziomou.data.PublicMatchReadRepository;
import io.github.eziomou.data.PublicMatchWriteRepository;
import io.github.eziomou.data.Synchronizer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

public final class MongoPublicMatchRepository implements PublicMatchWriteRepository, PublicMatchReadRepository,
        Synchronizer<PublicMatchReadRepository> {

    private final MongoCollection<Document> matches;

    public MongoPublicMatchRepository(MongoDatabase database) {
        this.matches = database.getCollection("publicMatches");
    }

    @Override
    public Completable saveAll(List<? extends PublicMatch> matches) {
        return Completable.fromPublisher(this.matches.insertMany(matches.stream()
                .map(this::asDocument)
                .collect(Collectors.toList())));
    }

    private Document asDocument(PublicMatch match) {
        Document document = new Document();
        document.put("matchId", match.getMatchId());
        document.put("radiantWin", match.isRadiantWin());
        document.put("duration", match.getDuration());
        document.put("lobbyType", match.getLobbyType());
        document.put("gameMode", match.getGameMode());
        return document;
    }

    @Override
    public Observable<PublicMatch> findAllAsc() {
        return Observable.fromPublisher(matches.find()
                .sort(Sorts.ascending("matchId")))
                .map(this::asPublicMatch);
    }

    @Override
    public Observable<PublicMatch> findAllAscAboveId(long matchId) {
        return Observable.fromPublisher(matches.find(gt("matchId", matchId))
                .sort(Sorts.ascending("matchId")))
                .map(this::asPublicMatch);
    }

    @Override
    public Observable<PublicMatch> findAllDesc() {
        return Observable.fromPublisher(matches.find()
                .sort(Sorts.descending("matchId")))
                .map(this::asPublicMatch);
    }

    @Override
    public Observable<PublicMatch> findAllDescBelowId(long matchId) {
        return Observable.fromPublisher(matches.find(lt("matchId", matchId))
                .sort(Sorts.descending("matchId")))
                .map(this::asPublicMatch);
    }

    private PublicMatch asPublicMatch(Document document) {
        return new PublicMatch(getId(document), document.getBoolean("radiantWin"),
                document.getInteger("duration"), document.getInteger("lobbyType"),
                document.getInteger("gameMode"));
    }

    private Long getId(Document document) {
        return document.getLong("matchId");
    }

    @Override
    public Maybe<Long> findMaxId() {
        return Observable.fromPublisher(matches.find().sort(Sorts.descending("matchId")).first())
                .firstElement().map(this::getId);
    }

    @Override
    public Maybe<Long> findMinId() {
        return Observable.fromPublisher(matches.find().sort(Sorts.ascending("matchId")).first())
                .firstElement().map(this::getId);
    }

    @Override
    public Completable synchronize(PublicMatchReadRepository source) {
        return findMaxId().flatMapObservable(source::findAllAscAboveId)
                .buffer(100_000)
                .flatMapCompletable(this::saveAll);
    }
}
