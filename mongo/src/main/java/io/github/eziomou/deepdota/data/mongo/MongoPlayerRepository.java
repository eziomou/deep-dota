package io.github.eziomou.deepdota.data.mongo;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.data.Player;
import io.github.eziomou.data.PlayerReadRepository;
import io.github.eziomou.data.PlayerWriteRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public final class MongoPlayerRepository implements PlayerWriteRepository, PlayerReadRepository {

    private final MongoCollection<Document> players;
    private final PlayerMapper playerMapper = new PlayerMapper();

    public MongoPlayerRepository(MongoDatabase database) {
        this.players = database.getCollection("players");
    }

    @Override
    public Completable saveAll(List<? extends Player> players) {
        return Completable.fromPublisher(this.players.insertMany(players.stream()
                .map(playerMapper::mapToDocument)
                .collect(Collectors.toList())));
    }

    @Override
    public Maybe<Player> findOneByAccountIdAndHeroId(long accountId, int heroId) {
        return Maybe.fromPublisher(players.find(and(eq("accountId", accountId), eq("heroId", heroId))))
                .map(playerMapper::mapToPlayer);
    }
}
