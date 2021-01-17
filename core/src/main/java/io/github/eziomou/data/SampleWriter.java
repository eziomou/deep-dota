package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface SampleWriter<T> {

    Completable write(Observable<T> samples);
}
