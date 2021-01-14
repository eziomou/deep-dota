package io.github.eziomou.predict;

final class PredictionImpl implements Prediction {

    private final double radiantWinProbability;
    private final double direWinProbability;

    PredictionImpl(double radiantWinProbability, double direWinProbability) {
        this.radiantWinProbability = radiantWinProbability;
        this.direWinProbability = direWinProbability;
    }

    @Override
    public double getRadiantWinProbability() {
        return radiantWinProbability;
    }

    @Override
    public double getDireWinProbability() {
        return direWinProbability;
    }

    @Override
    public String toString() {
        return String.format("[radiant=%.2f%%;dire=%.2f%%]", radiantWinProbability * 100, direWinProbability * 100);
    }
}
