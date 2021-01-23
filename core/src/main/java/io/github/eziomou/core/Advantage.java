package io.github.eziomou.core;

import io.github.eziomou.data.FullPublicMatch;
import io.github.eziomou.data.Pair;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;

public class Advantage extends Pair<INDArray, INDArray> {

    private static final long[] SHAPE = new long[]{130, 130};

    public Advantage(INDArray synergyMatrix, INDArray counterMatrix) {
        super(synergyMatrix, counterMatrix);
        validateSynergyMatrix(synergyMatrix);
        validateCounterMatrix(counterMatrix);
    }

    private void validateSynergyMatrix(INDArray synergyMatrix) {
        validateShape(synergyMatrix, "Synergy matrix has invalid shape: " + Arrays.toString(synergyMatrix.shape()));
    }

    private void validateCounterMatrix(INDArray counterMatrix) {
        validateShape(counterMatrix, "Counter matrix has invalid shape: " + Arrays.toString(counterMatrix.shape()));
    }

    private void validateShape(INDArray matrix, String message) {
        if (!Arrays.equals(matrix.shape(), SHAPE)) {
            throw new IllegalArgumentException(message);
        }
    }

    public INDArray getSynergyMatrix() {
        return getFirst();
    }

    public INDArray getCounterMatrix() {
        return getSecond();
    }

    public static Single<Advantage> create(Observable<? extends FullPublicMatch> matches) {
        return matches.collect(AdvantageAccumulator::new, AdvantageAccumulator::add).map(AdvantageAccumulator::create);
    }

    public static Advantage create(INDArray synergyMatrix, INDArray counterMatrix) {
        return new Advantage(synergyMatrix, counterMatrix);
    }

    public static Advantage load(String synergyMatrixPath, String counterMatrixPath) {
        return create(Nd4j.readTxt(synergyMatrixPath), Nd4j.readTxt(counterMatrixPath));
    }
}
