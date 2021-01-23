package io.github.eziomou.deepdota.example;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.core.*;
import io.github.eziomou.data.*;
import io.github.eziomou.deepdota.data.mongo.MongoFullMatchRepository;
import io.github.eziomou.deepdota.data.mongo.MongoFullPublicMatchRepository;
import io.github.eziomou.deepdota.data.mongo.MongoPublicMatchRepository;
import io.github.eziomou.deepdota.data.mongo.MongoPublicPlayerMatchRepository;
import io.github.eziomou.deepdota.data.opendota.OpenDotaPublicMatchRepository;
import io.github.eziomou.deepdota.data.opendota.OpenDotaPublicPlayerMatchRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.StandardizeSerializerStrategy;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Example {

    private static final Logger log = LoggerFactory.getLogger(Example.class);

    private static final String BASE_PATH = "example/src/main/resources";

    private static final String TRAIN_DATA_PATH = BASE_PATH + "/train-data.csv";
    private static final String TEST_DATA_PATH = BASE_PATH + "/test-data.csv";

    private static final String MODEL_PATH = BASE_PATH + "/model.bin";
    private static final String STATISTICS_PATH = BASE_PATH + "/statistics.bin";

    private static final String SYNERGY_MATRIX_PATH = BASE_PATH + "/synergy.txt";
    private static final String COUNTER_MATRIX_PATH = BASE_PATH + "/counter.txt";

    private static final int BATCH_SIZE = 128;
    private static final int EPOCHS_COUNT = 20;

    private final MongoClient mongoClient = MongoClients.create();
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("dota");
    private final FullPublicMatchRepository fullPublicMatchRepository = new MongoFullPublicMatchRepository(mongoDatabase);

    private final Model10X10 model = new Model10X10(1e-3, 1e-4);
    private final Model10X10SampleFactory factory = new Model10X10SampleFactory(Advantage.load(SYNERGY_MATRIX_PATH, COUNTER_MATRIX_PATH));
    private final ModelTrainer<Model10X10> trainer = GenericModelTrainer.builder()
            .trainIterator(Iterators.newCsvIterator(new File(TRAIN_DATA_PATH), model, BATCH_SIZE))
            .testIterator(Iterators.newCsvIterator(new File(TEST_DATA_PATH), model, BATCH_SIZE))
            .statistics(new File(STATISTICS_PATH))
            .epochsCount(EPOCHS_COUNT)
            .build();

    public Example() throws IOException, InterruptedException {
    }

    public static void main(String[] args) throws Exception {
        Example example = new Example();
//        example.fetchPublicMatches().blockingSubscribe();
//        example.fetchPublicPlayerMatches().blockingSubscribe();
//        example.createData().blockingSubscribe();
//        example.train().blockingSubscribe();
//        example.createData().andThen(example.train()).blockingSubscribe();

        example.evaluateModel().blockingSubscribe();
    }

    private Completable fetchPublicMatches() {
        PublicMatchReadRepository in = new OpenDotaPublicMatchRepository();
        PublicMatchWriteRepository out = new MongoPublicMatchRepository(mongoDatabase);
        return in.findAllDescBelowId(5780803017L)
                .take(1_000_000)
                .buffer(200_000)
                .doOnNext(matches -> log.info("Storing {} public matches", matches.size()))
                .flatMapCompletable(out::saveAll);
    }

    private Completable fetchPublicPlayerMatches() {
        PublicPlayerMatchReadRepository in = new OpenDotaPublicPlayerMatchRepository();
        PublicPlayerMatchWriteRepository out = new MongoPublicPlayerMatchRepository(mongoDatabase);
        return in.findAllDescBelowId(5780803017L)
                .take(10_000_000)
                .buffer(200_000)
                .doOnNext(matches -> log.info("Storing {} public player matches", matches.size()))
                .flatMapCompletable(out::saveAll);
    }

    private Completable createData() {
        return Completable.mergeArray(createTrainData(), createTestData());
    }

    private Completable createTrainData() {
        SampleWriter<INDArray> writer = new CsvSampleWriter(new File(TRAIN_DATA_PATH));
        return writer.write(fullPublicMatchRepository.findAllDesc()
                .skip(1_000)
                .take(9_000)
                .flatMapSingle(factory::create))
                .doOnSubscribe(s -> log.info("Creating training data: " + TRAIN_DATA_PATH))
                .doOnComplete(() -> log.info("Created training data: " + TRAIN_DATA_PATH))
                .subscribeOn(Schedulers.io());
    }

    private Completable createTestData() {
        SampleWriter<INDArray> writer = new CsvSampleWriter(new File(TEST_DATA_PATH));
        return writer.write(fullPublicMatchRepository.findAllDesc()
                .take(1_000)
                .flatMapSingle(factory::create))
                .doOnSubscribe(s -> log.info("Creating test data: " + TEST_DATA_PATH))
                .doOnComplete(() -> log.info("Created test data: " + TEST_DATA_PATH))
                .subscribeOn(Schedulers.io());
    }

    private Completable createAdvantage() {
        return Advantage.create(fullPublicMatchRepository.findAllDesc().skip(1_000))
                .doOnSuccess(statistics -> {
                    Nd4j.writeTxt(statistics.getSynergyMatrix(), SYNERGY_MATRIX_PATH);
                    log.info("Created synergy matrix: " + SYNERGY_MATRIX_PATH);

                    Nd4j.writeTxt(statistics.getCounterMatrix(), COUNTER_MATRIX_PATH);
                    log.info("Created counter matrix: " + COUNTER_MATRIX_PATH);
                })
                .ignoreElement();
    }

    public Completable trainModel() {
        return Single
                .fromCallable(() -> {
                    startServer(model);
                    return model;
                })
                .flatMap(trainer::train)
                .doOnSuccess(model -> {
                    model.getNetwork().save(new File(MODEL_PATH));
                    log.info("Saved model: " + MODEL_PATH);
                })
                .ignoreElement();
    }

    private void startServer(Model model) {
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.getNetwork().setListeners(new StatsListener(statsStorage));
    }

    public Completable evaluateModel() {
        return Completable.create(emitter -> {
            MultiLayerNetwork network = MultiLayerNetwork.load(new File(MODEL_PATH), true);

            DataSetIterator trainIterator = Iterators.newCsvIterator(new File(TRAIN_DATA_PATH), model, BATCH_SIZE);
            DataSetIterator testIterator = Iterators.newCsvIterator(new File(TEST_DATA_PATH), model, BATCH_SIZE);

            DataNormalization normalizer = new NormalizerStandardize();
            normalizer.fit(trainIterator);

            NormalizerSerializer serializer = new NormalizerSerializer().addStrategy(new StandardizeSerializerStrategy());
            serializer.write(normalizer, STATISTICS_PATH);

            trainIterator.setPreProcessor(normalizer);
            testIterator.setPreProcessor(normalizer);

            log.info(network.evaluate(testIterator).stats());

            emitter.onComplete();
        });
    }
}
