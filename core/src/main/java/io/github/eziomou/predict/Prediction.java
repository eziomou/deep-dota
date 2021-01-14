package io.github.eziomou.predict;

public interface Prediction {

    double getRadiantWinProbability();

    double getDireWinProbability();

    static Prediction create(double radiantWinProbability, double direWinProbability) {
        return new PredictionImpl(radiantWinProbability, direWinProbability);
    }
}
