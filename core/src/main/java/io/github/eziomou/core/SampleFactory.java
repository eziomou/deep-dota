package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Single;

public interface SampleFactory<I, O> {

    Single<O> create(I input);
}
