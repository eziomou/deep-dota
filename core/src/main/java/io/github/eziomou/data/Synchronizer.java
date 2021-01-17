package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;

public interface Synchronizer<S> {

    Completable synchronize(S source);
}
