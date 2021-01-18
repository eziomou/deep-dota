package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;

import java.util.List;

public interface PublicPlayerMatchWriteRepository {

    Completable saveAll(List<? extends PublicPlayerMatch> playerMatches);
}
