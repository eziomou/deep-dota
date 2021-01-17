package io.github.eziomou.deepdota.example;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.core.*;
import io.github.eziomou.data.CsvSampleWriter;
import io.github.eziomou.data.FullMatch;
import io.github.eziomou.data.FullMatchRepository;
import io.github.eziomou.data.SampleWriter;
import io.github.eziomou.deepdota.data.mongo.MongoFullMatchRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

public class Example {

    private static final String TRAIN_DATA_PATH = "example/src/main/resources/train-data.csv";
    private static final String TEST_DATA_PATH = "example/src/main/resources/test-data.csv";

    private static final String MODEL_PATH = "example/src/main/resources/model";

    private static final int BATCH_SIZE = 1024;
    private static final int EPOCHS_COUNT = 10;

    private final MongoClient mongoClient = MongoClients.create();
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("dota");

    private static final long id = 5769920592L;

    private final FullMatchRepository fullMatchRepository = new MongoFullMatchRepository(mongoDatabase);
    private final StatsService statsService = new StatsService(fullMatchRepository.findAllDescBelowId(id).take(50_000));
    private final SampleFactory<FullMatch, INDArray> factory = new Model10X10SampleFactory(statsService);

    public static void main(String[] args) {
        Example example = new Example();
        example.createData().andThen(example.train()).blockingSubscribe();
    }

    private Completable createData() {
        return Completable.mergeArray(
                createTrainData()
                        .doOnComplete(() -> System.out.println("train complete"))
                        .subscribeOn(Schedulers.io()),
                createTestData()
                        .doOnComplete(() -> System.out.println("test complete"))
                        .subscribeOn(Schedulers.io())
        );
    }

    private Completable createTrainData() {
        SampleWriter<INDArray> writer = new CsvSampleWriter(new File(TRAIN_DATA_PATH));
        return writer.write(fullMatchRepository.findAllDescBelowId(id).take(50_000).flatMapSingle(factory::create));
    }

    private Completable createTestData() {
        SampleWriter<INDArray> writer = new CsvSampleWriter(new File(TEST_DATA_PATH));
        return writer.write(fullMatchRepository.findAllAscAboveId(id).take(1_000).flatMapSingle(factory::create));
    }

    public Completable train() {
        return Single.fromCallable(() -> {
            UIServer uiServer = UIServer.getInstance();
            StatsStorage statsStorage = new InMemoryStatsStorage();
            uiServer.attach(statsStorage);

            Model10X10 model = new Model10X10();
            model.getNetwork().setListeners(new StatsListener(statsStorage));

            return model;
        }).flatMap(model -> {
            ModelTrainer<Model10X10> trainer = Model10X10Trainer.builder()
                    .trainData(new File(TRAIN_DATA_PATH))
                    .testData(new File(TEST_DATA_PATH))
                    .batchSize(BATCH_SIZE)
                    .epochsCount(EPOCHS_COUNT)
                    .build();
            return trainer.train(model).toSingle(() -> model);
        }).doOnSuccess(model -> model.saveModel(new File(MODEL_PATH))).ignoreElement();
    }
}
