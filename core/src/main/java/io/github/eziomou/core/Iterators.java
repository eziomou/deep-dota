package io.github.eziomou.core;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;

public final class Iterators {

    private Iterators() {
    }

    public static DataSetIterator newCsvIterator(File file, Model model, int batchSize)
            throws IOException, InterruptedException {
        return new RecordReaderDataSetIterator.Builder(gerReader(file), batchSize)
                .classification(model.getInputsNumber(), model.getOutputsNumber())
                .build();
    }

    public static DataSetIterator newCsvIterator(File file, int inputsNumber, int outputsNumber, int batchSize)
            throws IOException, InterruptedException {
        return new RecordReaderDataSetIterator.Builder(gerReader(file), batchSize)
                .classification(inputsNumber, outputsNumber)
                .build();
    }

    private static RecordReader gerReader(File file) throws IOException, InterruptedException {
        RecordReader reader = new CSVRecordReader(',');
        reader.initialize(new FileSplit(file));
        return reader;
    }
}
