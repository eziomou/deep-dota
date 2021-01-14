package io.github.eziomou.predict;

import io.github.eziomou.data.Team;
import io.reactivex.rxjava3.core.Single;

public interface PredictionService {

    Single<Prediction> predict(Team radiant, Team dire);
}
