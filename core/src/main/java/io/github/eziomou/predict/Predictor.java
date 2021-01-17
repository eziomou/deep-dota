package io.github.eziomou.predict;

import io.github.eziomou.data.Hero;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface Predictor {

    Single<Prediction> predict(List<? extends Hero> radiant, List<? extends Hero> dire);
}
