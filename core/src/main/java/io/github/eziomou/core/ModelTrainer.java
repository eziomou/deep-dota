package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Single;

public interface ModelTrainer<M> {

    Single<Model> train(M model);
}
