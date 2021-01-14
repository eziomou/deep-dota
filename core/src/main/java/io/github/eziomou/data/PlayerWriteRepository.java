package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;

import java.util.List;

public interface PlayerWriteRepository {

    Completable saveAll(List<? extends Player> players);
}
