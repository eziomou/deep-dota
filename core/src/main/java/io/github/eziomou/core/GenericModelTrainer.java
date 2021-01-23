package io.github.eziomou.core;

import io.reactivex.rxjava3.core.Single;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.StandardizeSerializerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GenericModelTrainer<M extends Model> implements ModelTrainer<M> {

    private static final Logger log = LoggerFactory.getLogger(GenericModelTrainer.class);

    private static final int DEFAULT_EPOCHS_COUNT = 10;

    private final int epochsCount;

    private final DataSetIterator trainIterator;
    private final DataSetIterator testIterator;
    private final File statistics;

    private GenericModelTrainer(Builder builder) {
        this.trainIterator = builder.trainIterator;
        this.testIterator = builder.testIterator;
        this.statistics = builder.statistics;
        this.epochsCount = builder.epochsCount;
    }

    @Override
    public Single<Model> train(M model) {
        return Single.create(emitter -> {
            DataNormalization normalizer = new NormalizerStandardize();
            normalizer.fit(trainIterator);

            if (statistics != null) {
                NormalizerSerializer serializer = new NormalizerSerializer()
                        .addStrategy(new StandardizeSerializerStrategy());
                serializer.write(normalizer, statistics);
            }

            trainIterator.setPreProcessor(normalizer);
            if (testIterator != null) {
                testIterator.setPreProcessor(normalizer);
            }

            log.info("Starting training: " + model.getClass().getName());
            model.getNetwork().fit(trainIterator, epochsCount);
            log.info("Completed training: " + model.getClass().getName());

            if (testIterator != null) {
                Evaluation evaluation = model.getNetwork().evaluate(testIterator);
                log.info(evaluation.stats());
            }
            emitter.onSuccess(model);
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private DataSetIterator trainIterator;
        private DataSetIterator testIterator;
        private File statistics;

        private int epochsCount = DEFAULT_EPOCHS_COUNT;

        public Builder trainIterator(DataSetIterator trainIterator) {
            this.trainIterator = trainIterator;
            return this;
        }

        public Builder testIterator(DataSetIterator testIterator) {
            this.testIterator = testIterator;
            return this;
        }

        public Builder statistics(File statistics) {
            this.statistics = statistics;
            return this;
        }

        public Builder epochsCount(int epochsCount) {
            this.epochsCount = epochsCount;
            return this;
        }

        public <M extends Model> GenericModelTrainer<M> build() {
            return new GenericModelTrainer<>(this);
        }
    }
}
