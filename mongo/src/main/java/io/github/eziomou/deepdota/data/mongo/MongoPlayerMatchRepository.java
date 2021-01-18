package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.PlayerMatch;
import io.github.eziomou.data.PlayerMatchReadRepository;
import io.github.eziomou.data.PlayerMatchWriteRepository;
import io.github.eziomou.data.Synchronizer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.gt;

public final class MongoPlayerMatchRepository implements PlayerMatchWriteRepository, PlayerMatchReadRepository,
        Synchronizer<PlayerMatchReadRepository> {

    private final MongoCollection<Document> playerMatches;
    private final PlayerMatchMapper playerMatchMapper = new PlayerMatchMapper();

    public MongoPlayerMatchRepository(MongoDatabase database) {
        this.playerMatches = database.getCollection("playerMatches");
    }

    @Override
    public Completable saveAll(List<? extends PlayerMatch> playerMatches) {
        return Completable.fromPublisher(this.playerMatches.insertMany(playerMatches.stream()
                .map(this::asDocument)
                .collect(Collectors.toList())));
    }

    private Document asDocument(PlayerMatch playerMatch) {
        Document document = new Document();
        document.put("matchId", playerMatch.getMatchId());
        document.put("accountId", playerMatch.getAccountId());
        document.put("playerSlot", playerMatch.getPlayerSlot());
        document.put("heroId", playerMatch.getHeroId());
        return document;
    }

    @Override
    public Observable<PlayerMatch> findAllAsc() {
        return Observable.fromPublisher(playerMatches.find()
                .sort(Sorts.ascending("matchId")))
                .map(playerMatchMapper::asPlayerMatch);
    }

    @Override
    public Observable<PlayerMatch> findAllAscAboveId(long matchId) {
        return Observable.fromPublisher(playerMatches.find(gt("matchId", matchId))
                .sort(Sorts.ascending("matchId")))
                .map(playerMatchMapper::asPlayerMatch);
    }

    @Override
    public Completable synchronize(PlayerMatchReadRepository source) {
        return source.findAllAsc().toList().concatMapCompletable(this::saveAll);
    }
}
