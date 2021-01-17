package io.github.eziomou.predict;

public interface Prediction {

    double getRadiant();

    double getDire();

    static Prediction create(double radiantWinProbability, double direWinProbability) {
        return new PredictionImpl(radiantWinProbability, direWinProbability);
    }
}
