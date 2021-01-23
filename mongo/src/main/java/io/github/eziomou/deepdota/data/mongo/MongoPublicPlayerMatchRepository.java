package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.PublicPlayerMatch;
import io.github.eziomou.data.PublicPlayerMatchReadRepository;
import io.github.eziomou.data.PublicPlayerMatchWriteRepository;
import io.github.eziomou.data.Synchronizer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

public final class MongoPublicPlayerMatchRepository implements PublicPlayerMatchWriteRepository, PublicPlayerMatchReadRepository,
        Synchronizer<PublicPlayerMatchReadRepository> {

    private final MongoCollection<Document> playerMatches;
    private final PublicPlayerMatchMapper playerMatchMapper = new PublicPlayerMatchMapper();

    public MongoPublicPlayerMatchRepository(MongoDatabase database) {
        this.playerMatches = database.getCollection("publicPlayerMatches");
    }

    @Override
    public Completable saveAll(List<? extends PublicPlayerMatch> playerMatches) {
        return Completable.fromPublisher(this.playerMatches.insertMany(playerMatches.stream()
                .map(this::asDocument)
                .collect(Collectors.toList())));
    }

    private Document asDocument(PublicPlayerMatch playerMatch) {
        Document document = new Document();
        document.put("matchId", playerMatch.getMatchId());
        document.put("playerSlot", playerMatch.getPlayerSlot());
        document.put("heroId", playerMatch.getHeroId());
        return document;
    }

    @Override
    public Observable<PublicPlayerMatch> findAllAsc() {
        return Observable.fromPublisher(playerMatches.find()
                .sort(Sorts.ascending("matchId")))
                .map(playerMatchMapper::asPublicPlayerMatch);
    }

    @Override
    public Observable<PublicPlayerMatch> findAllAscAboveId(long matchId) {
        return Observable.fromPublisher(playerMatches.find(gt("matchId", matchId))
                .sort(Sorts.ascending("matchId")))
                .map(playerMatchMapper::asPublicPlayerMatch);
    }

    @Override
    public Observable<PublicPlayerMatch> findAllDesc() {
        return Observable.fromPublisher(playerMatches.find()
                .sort(Sorts.descending("matchId")))
                .map(playerMatchMapper::asPublicPlayerMatch);
    }

    @Override
    public Observable<PublicPlayerMatch> findAllDescBelowId(long matchId) {
        return Observable.fromPublisher(playerMatches.find(lt("matchId", matchId))
                .sort(Sorts.descending("matchId")))
                .map(playerMatchMapper::asPublicPlayerMatch);
    }

    @Override
    public Maybe<Long> findMaxId() {
        return Observable.fromPublisher(playerMatches.find().sort(Sorts.descending("matchId")).first())
                .firstElement().map(this::getId);
    }

    @Override
    public Maybe<Long> findMinId() {
        return Observable.fromPublisher(playerMatches.find().sort(Sorts.ascending("matchId")).first())
                .firstElement().map(this::getId);
    }

    private Long getId(Document document) {
        return document.getLong("matchId");
    }

    @Override
    public Completable synchronize(PublicPlayerMatchReadRepository source) {
        return findMaxId().flatMapObservable(source::findAllAscAboveId)
                .buffer(100_000)
                .flatMapCompletable(this::saveAll);
    }
}
