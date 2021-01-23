package io.github.eziomou.core;

import io.github.eziomou.data.FullPublicMatch;
import io.github.eziomou.data.Team;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

class AdvantageAccumulator {

    private static final Logger log = LoggerFactory.getLogger(AdvantageAccumulator.class);

    private static final int MAX_HERO_ID = 130;

    /* 130x130 symmetrical matrices */
    private final INDArray synergyWins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
    private final INDArray synergyLosses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);

    /* 130x130 unsymmetrical matrices */
    private final INDArray counterWins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
    private final INDArray counterLosses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);

    private final AtomicInteger i = new AtomicInteger();

    public void add(FullPublicMatch match) {
        if (i.incrementAndGet() % 100_000 == 0) {
            log.debug("Iteration: " + i.get());
        }
        match.getPlayers().forEach(p1 -> match.getPlayers().forEach(p2 -> {
            if (p1.getHeroId() != 0 && p2.getHeroId() != 0) {
                INDArray source;
                if (Team.isSameTeam(p1, p2)) {
                    source = match.isWinner(p1) ? synergyWins : synergyLosses;
                } else {
                    source = match.isWinner(p1) ? counterWins : counterLosses;
                }
                double value = source.getDouble(p1.getHeroId() - 1, p2.getHeroId() - 1);
                source.putScalar(p1.getHeroId() - 1, p2.getHeroId() - 1, value + 1);
            }
        }));
    }

    public Advantage create() {
        INDArray synergyMatrix = synergyWins.div(synergyWins.add(synergyLosses));
        INDArray counterMatrix = counterWins.div(counterWins.add(counterLosses));
        return Advantage.create(synergyMatrix, counterMatrix);
    }

}
