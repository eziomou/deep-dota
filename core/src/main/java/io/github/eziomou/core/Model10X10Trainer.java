package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Completable;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.StandardizeSerializerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class Model10X10Trainer implements ModelTrainer<Model10X10> {

    private static final Logger log = LoggerFactory.getLogger(Model10X10Trainer.class);

    private final int batchSize;
    private final int epochsCount;

    private final File trainData;
    private final File testData;

    private Model10X10Trainer(Builder builder) {
        this.trainData = builder.trainData;
        this.testData = builder.testData;
        this.batchSize = builder.batchSize;
        this.epochsCount = builder.epochsCount;
    }

    @Override
    public Completable train(Model10X10 model) {
        return Completable.create(emitter -> {
            DataSetIterator trainIterator = getIterator(trainData);
            DataSetIterator testIterator = null;
            if (testData != null) {
                testIterator = getIterator(testData);
            }

            DataNormalization normalizer = new NormalizerStandardize();
            normalizer.fit(trainIterator);

            NormalizerSerializer serializer = new NormalizerSerializer()
                    .addStrategy(new StandardizeSerializerStrategy());
            serializer.write(normalizer, new File("example/src/main/resources/statistics"));

            trainIterator.setPreProcessor(normalizer);
            if (testIterator != null) {
                testIterator.setPreProcessor(normalizer);
            }

            model.getNetwork().fit(trainIterator, epochsCount);

            if (testIterator != null) {
                Evaluation evaluation = model.getNetwork().evaluate(testIterator);
                log.info(evaluation.stats());
            }
            emitter.onComplete();
        });
    }

    private DataSetIterator getIterator(File file) throws IOException, InterruptedException {
        RecordReader reader = new CSVRecordReader(',');
        reader.initialize(new FileSplit(file));
        return new RecordReaderDataSetIterator.Builder(reader, batchSize)
                .classification(Model10X10.INPUTS_NUMBER, Model10X10.OUTPUTS_NUMBER)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private File trainData;
        private File testData;

        private int batchSize;
        private int epochsCount;

        public Builder trainData(File trainData) {
            this.trainData = trainData;
            return this;
        }

        public Builder testData(File testData) {
            this.testData = testData;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder epochsCount(int epochsCount) {
            this.epochsCount = epochsCount;
            return this;
        }

        public Model10X10Trainer build() {
            return new Model10X10Trainer(this);
        }
    }
}
