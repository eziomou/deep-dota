package io.github.eziomou.deepdota.webapi;

import java.util.List;

public class PredictionRequest {

    private List<Integer> radiant;
    private List<Integer> dire;

    public List<Integer> getRadiant() {
        return radiant;
    }

    public void setRadiant(List<Integer> radiant) {
        this.radiant = radiant;
    }

    public List<Integer> getDire() {
        return dire;
    }

    public void setDire(List<Integer> dire) {
        this.dire = dire;
    }
}
