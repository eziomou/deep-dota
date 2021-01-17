package io.github.eziomou.core;

import io.github.eziomou.data.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Stats extends Pair<INDArray, INDArray> {

    public Stats(INDArray synergyMatrix, INDArray counterMatrix) {
        super(synergyMatrix, counterMatrix);
    }

    public INDArray getSynergyMatrix() {
        return getFirst();
    }

    public INDArray getCounterMatrix() {
        return getSecond();
    }

    public static Stats create(INDArray synergyMatrix, INDArray counterMatrix) {
        return new Stats(synergyMatrix, counterMatrix);
    }
}
