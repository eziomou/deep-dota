package io.github.eziomou.data;

import java.util.List;

public interface Team extends Iterable<Player> {

    static Team create(List<Player> players) {
        return new TeamImpl(players);
    }

    static Team create(Player... players) {
        return new TeamImpl(List.of(players), false);
    }
}
