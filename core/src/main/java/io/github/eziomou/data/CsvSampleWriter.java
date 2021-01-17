package io.github.eziomou.data;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.*;

public class CsvSampleWriter implements SampleWriter<INDArray> {

    private final File file;

    public CsvSampleWriter(File file) {
        this.file = file;
    }

    @Override
    public Completable write(Observable<INDArray> samples) {
        return Completable.defer(() -> {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            return samples.doOnNext(sample -> write(sample, writer)).doOnComplete(writer::close).ignoreElements();
        });
    }

    private void write(INDArray sample, Writer writer) throws IOException {
        for (int j = 0; j < sample.size(0) - 1; j++) {
            writer.write(sample.getDouble(j) + ",");
        }
        writer.write(sample.getDouble(sample.size(0) - 1) + "\n");
    }
}
