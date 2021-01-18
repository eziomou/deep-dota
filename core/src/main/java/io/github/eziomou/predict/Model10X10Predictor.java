package io.github.eziomou.predict;

import io.github.eziomou.core.Model10X10;
import io.github.eziomou.core.StatsService;
import io.github.eziomou.data.Hero;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

public final class Model10X10Predictor implements Predictor {

    private final Model10X10 model;
    private final DataNormalization normalizer;
    private final StatsService statsService;

    public Model10X10Predictor(Model10X10 model, DataNormalization normalizer, StatsService statsService) {
        this.model = model;
        this.normalizer = normalizer;
        this.statsService = statsService;
    }

    @Override
    public Single<Prediction> predict(List<? extends Hero> radiant, List<? extends Hero> dire) {
        return statsService.getStats()
                .map(stats -> {
                    INDArray input = createInput(radiant, dire, stats.getSynergyMatrix(), stats.getCounterMatrix());
                    INDArray output = model.getNetwork().output(input);
                    return Prediction.create(output.getDouble(0, 1), output.getDouble(0, 0));
                })
                .subscribeOn(Schedulers.single());
    }

    private INDArray createInput(List<? extends Hero> radiant, List<? extends Hero> dire,
                                 INDArray synergyMatrix, INDArray counterMatrix) {
        List<Hero> heroes = new ArrayList<>(radiant);
        heroes.addAll(dire);
        INDArray input10X10 = Nd4j.zeros(10, 10);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                INDArray source = (i < 5 && j < 5 || i >= 5 && j >= 5) ? synergyMatrix : counterMatrix;
                double ratio = source.getDouble(heroes.get(i).getId() - 1, heroes.get(j).getId() - 1);
                if (Double.isNaN(ratio)) {
                    ratio = 0.5;
                }
                input10X10.putScalar(i, j, ratio);
            }
        }
        INDArray input1X100 = input10X10.reshape(1, 100);
        normalizer.transform(input1X100);
        return input1X100;
    }
}
