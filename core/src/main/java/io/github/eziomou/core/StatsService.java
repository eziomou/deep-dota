package io.github.eziomou.core;

import io.github.eziomou.data.FullPublicMatch;
import io.github.eziomou.data.Team;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.SingleSubject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.concurrent.atomic.AtomicBoolean;

public class StatsService {

    private final Observable<? extends FullPublicMatch> matches;

    private final SingleSubject<Stats> subject = SingleSubject.create();
    private final AtomicBoolean requested = new AtomicBoolean(false);

    public StatsService(Observable<? extends FullPublicMatch> matches) {
        this.matches = matches;
    }

    public Single<Stats> getStats() {
        return Single.defer(() -> {
            if (requested.compareAndSet(false, true)) {
                return createAdvantageMatrices(matches).doOnSuccess(subject::onSuccess);
            }
            return subject;
        });
    }

    public static Single<Stats> createAdvantageMatrices(Observable<? extends FullPublicMatch> matches) {
        return matches.collect(StatsAccumulator::new, StatsAccumulator::add).map(StatsAccumulator::create);
    }

    private static class StatsAccumulator {

        private static final int MAX_HERO_ID = 130;

        /* 130x130 symmetrical matrices */
        private final INDArray synergyWins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        private final INDArray synergyLosses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);

        /* 130x130 unsymmetrical matrices */
        private final INDArray counterWins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        private final INDArray counterLosses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);

        void add(FullPublicMatch match) {
            match.getPlayers().forEach(p1 -> match.getPlayers().forEach(p2 -> {
                INDArray source;
                if (Team.isSameTeam(p1, p2)) {
                    source = match.isWinner(p1) ? synergyWins : synergyLosses;
                } else {
                    source = match.isWinner(p1) ? counterWins : counterLosses;
                }
                double value = source.getDouble(p1.getHeroId() - 1, p2.getHeroId() - 1);
                source.putScalar(p1.getHeroId() - 1, p2.getHeroId() - 1, value + 1);
            }));
        }

        private Stats create() {
            INDArray synergyMatrix = synergyWins.div(synergyWins.add(synergyLosses));
            INDArray counterMatrix = counterWins.div(counterWins.add(counterLosses));
            return Stats.create(synergyMatrix, counterMatrix);
        }
    }
}
