package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;

import java.util.List;

public interface PublicMatchWriteRepository {

    Completable saveAll(List<? extends PublicMatch> matches);
}
