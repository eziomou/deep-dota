package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public final class Model130Repository implements MatchWriteRepository {

    private final File file;

    public Model130Repository(File file) {
        this.file = file;
    }

    @Override
    public Completable saveAll(List<? extends Match> matches) {
        return Completable.create(emitter -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (Match match : matches) {
                    writer.write(toString(match) + '\n');
                }
            }
            emitter.onComplete();
        });
    }

    private String toString(Match match) {
        double[] inputs = new double[130];
        Arrays.fill(inputs, 0);
        for (Player player : match.getRadiant()) {
            inputs[player.getHeroId() - 1] = calcRatio(player);
        }
        for (Player player : match.getDire()) {
            inputs[player.getHeroId() - 1] = -1 * calcRatio(player);
        }
        StringBuilder sb = new StringBuilder();
        for (double input : inputs) {
            sb.append(input).append(',');
        }
        sb.append(match.isRadiantWin() ? 1 : 0);
        return sb.toString();
    }

    private double calcRatio(Player player) {
        return (double) player.getWonMatches() / player.getTotalMatches();
    }
}
