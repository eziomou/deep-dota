package io.github.eziomou.data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

public final class Statistics {

    private static final int MAX_HERO_ID = 130;

    private Statistics() {
    }

    public static INDArray createCounterMatrix(List<? extends FullMatch> matches) {
        INDArray wins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        INDArray loses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        matches.forEach(match -> match.getPlayers().forEach(p1 -> match.getPlayers().stream()
                .filter(p2 -> !Team.isSameTeam(p1, p2))
                .forEach(p2 -> {
                    INDArray target = match.isWinner(p1) ? wins : loses;
                    double value = target.getDouble(p1.getHeroId() - 1, p2.getHeroId() - 1);
                    target.putScalar(p1.getHeroId() - 1, p2.getHeroId() - 1, value + 1);
                })));
        return wins.div(wins.add(loses));
    }

    public static INDArray createSynergyMatrix(List<? extends FullMatch> matches) {
        INDArray wins = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        INDArray loses = Nd4j.zeros(MAX_HERO_ID, MAX_HERO_ID);
        matches.forEach(match -> match.getPlayers().forEach(p1 -> match.getPlayers().stream()
                .filter(p2 -> Team.isSameTeam(p1, p2))
                .forEach(p2 -> {
                    INDArray target = match.isWinner(p1) ? wins : loses;
                    double value = target.getDouble(p1.getHeroId() - 1, p2.getHeroId() - 1);
                    target.putScalar(p1.getHeroId() - 1, p2.getHeroId() - 1, value + 1);
                })));
        return wins.div(wins.add(loses));
    }
}
