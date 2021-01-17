package io.github.eziomou.predict;

final class PredictionImpl implements Prediction {

    private final double radiant;
    private final double dire;

    PredictionImpl(double radiant, double dire) {
        this.radiant = radiant;
        this.dire = dire;
    }

    @Override
    public double getRadiant() {
        return radiant;
    }

    @Override
    public double getDire() {
        return dire;
    }

    @Override
    public String toString() {
        return String.format("[radiant=%.2f%%;dire=%.2f%%]", radiant * 100, dire * 100);
    }
}
