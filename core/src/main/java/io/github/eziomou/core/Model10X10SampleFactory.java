package io.github.eziomou.core;

import io.github.eziomou.data.FullMatch;
import io.github.eziomou.data.PlayerMatch;
import io.reactivex.rxjava3.core.Single;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Model10X10SampleFactory implements SampleFactory<FullMatch, INDArray> {

    private final StatsService statsService;

    public Model10X10SampleFactory(StatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    public Single<INDArray> create(FullMatch match) {
        return statsService.getStats().map(stats -> {
            INDArray sample = Nd4j.zeros(10, 10);
            match.getPlayers().forEach(p1 -> match.getPlayers().forEach(p2 -> {
                INDArray source = isSameTeam(p1, p2) ? stats.getSynergyMatrix() : stats.getCounterMatrix();
                double ratio = source.getDouble(p1.getHeroId() - 1, p2.getHeroId() - 1);
                sample.putScalar(getOffset(p1) + p1.getPosition(), getOffset(p2) + p2.getPosition(), ratio);
            }));
            return Nd4j.concat(0, sample.reshape(Model10X10.INPUTS_NUMBER), getLabel(match));
        });
    }

    private boolean isSameTeam(PlayerMatch first, PlayerMatch second) {
        return first.isRadiant() == second.isRadiant();
    }

    private int getOffset(PlayerMatch player) {
        return player.isRadiant() ? 0 : 5;
    }

    private INDArray getLabel(FullMatch match) {
        return match.isRadiantWin() ? Nd4j.ones(1) : Nd4j.zeros(1);
    }
}
