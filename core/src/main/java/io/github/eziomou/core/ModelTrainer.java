package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Completable;

public interface ModelTrainer<M> {

    Completable train(M model);
}
