package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Completable;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.DataSetIteratorSplitter;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class Model130Trainer implements ModelTrainer<Model130> {

    private static final Logger log = LoggerFactory.getLogger(Model130Trainer.class);

    private final int samplesCount;
    private final int batchSize;
    private final double trainTestRatio;
    private final int epochsCount;

    private final DataSetIterator trainIterator;
    private final DataSetIterator testIterator;

    private Model130Trainer(Builder builder) throws IOException, InterruptedException {
        this.samplesCount = builder.samplesCount;
        this.batchSize = builder.batchSize;
        this.trainTestRatio = builder.trainTestRatio;
        this.epochsCount = builder.epochsCount;
        DataSetIteratorSplitter splitter = getSplitter(getIterator(builder.file));
        this.trainIterator = splitter.getTrainIterator();
        this.testIterator = splitter.getTestIterator();
    }

    @Override
    public Completable train(Model130 model) {
        return Completable.fromRunnable(() -> {
            log.info("Training started");
            model.getNetwork().fit(trainIterator, epochsCount);
            log.info("Training completed");
            Evaluation evaluation = model.getNetwork().evaluate(testIterator);
            log.info(evaluation.stats());
        });
    }

    private DataSetIterator getIterator(File file) throws IOException, InterruptedException {
        RecordReader reader = new CSVRecordReader(',');
        reader.initialize(new FileSplit(file));
        return new RecordReaderDataSetIterator.Builder(reader, batchSize)
                .classification(130, 2)
                .build();
    }

    private DataSetIteratorSplitter getSplitter(DataSetIterator iterator) {
        return new DataSetIteratorSplitter(iterator, samplesCount / batchSize, trainTestRatio);
    }

    public static Builder builder(File file) {
        return new Builder(file);
    }

    public static class Builder {

        private final File file;

        private int samplesCount;
        private int batchSize;
        private double trainTestRatio;
        private int epochsCount;

        private Builder(File file) {
            this.file = file;
        }

        public Builder samplesCount(int samplesCount) {
            this.samplesCount = samplesCount;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder trainTestRation(double trainTestRatio) {
            this.trainTestRatio = trainTestRatio;
            return this;
        }

        public Builder epochsCount(int epochsCount) {
            this.epochsCount = epochsCount;
            return this;
        }

        public Model130Trainer build() throws IOException, InterruptedException {
            return new Model130Trainer(this);
        }
    }
}
