package io.github.eziomou.predict;

import io.github.eziomou.core.Model130;
import io.github.eziomou.data.PlayerReadRepository;
import io.github.eziomou.data.Team;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.nd4j.linalg.factory.Nd4j;

public final class Model130PredictionService implements PredictionService {

    private final Model130 model;
    private final PlayerReadRepository players;

    public Model130PredictionService(Model130 model, PlayerReadRepository players) {
        this.model = model;
        this.players = players;
    }

    @Override
    public Single<Prediction> predict(Team radiant, Team dire) {
        return Observable.fromIterable(radiant)
                .flatMapMaybe(player -> players.findOneByAccountIdAndHeroId(player.getAccountId(), player.getHeroId()))
                .reduce(Nd4j.create(1, 130), (input, player) -> {
                    input.putScalar(player.getHeroId() - 1, (double) player.getWonMatches() / player.getTotalMatches());
                    return input;
                })
                .map(input -> model.getNetwork().output(input))
                .map(output -> Prediction.create(output.getDouble(0, 1), output.getDouble(0, 0)));
    }
}
