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
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Example {

    private static final Logger log = LoggerFactory.getLogger(Example.class);

    private static final String TRAIN_DATA_PATH = "example/src/main/resources/train-data.csv";
    private static final String TEST_DATA_PATH = "example/src/main/resources/test-data.csv";

    private static final String MODEL_PATH = "example/src/main/resources/model";

    private static final int BATCH_SIZE = 1024;
    private static final int EPOCHS_COUNT = 10;

    private final MongoClient mongoClient = MongoClients.create();
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("dota");

    private final FullMatchRepository fullMatchRepository = new MongoFullMatchRepository(mongoDatabase);
    private final FullPublicMatchRepository fullPublicMatchRepository = new MongoFullPublicMatchRepository(mongoDatabase);

    private final StatsService statsService = new StatsService(fullPublicMatchRepository.findAllAsc().take(50_000));
    private final SampleFactory<FullPublicMatch, INDArray> factory = new Model10X10SampleFactory(statsService);

    public static void main(String[] args) {
        Example example = new Example();
//        example.fetchPublicMatches().blockingSubscribe();
//        example.fetchPublicPlayerMatches().blockingSubscribe();
//        example.createData().blockingSubscribe();
        example.train().blockingSubscribe();
    }

    private Completable fetchPublicMatches() {
        PublicMatchReadRepository in = new OpenDotaPublicMatchRepository();
        PublicMatchWriteRepository out = new MongoPublicMatchRepository(mongoDatabase);
        return in.findAllDescBelowId(5792224709L)
                .take(1_000_000)
                .buffer(200_000)
                .doOnNext(matches -> log.info("Storing {} public matches", matches.size()))
                .flatMapCompletable(out::saveAll);
    }

    private Completable fetchPublicPlayerMatches() {
        PublicPlayerMatchReadRepository in = new OpenDotaPublicPlayerMatchRepository();
        PublicPlayerMatchWriteRepository out = new MongoPublicPlayerMatchRepository(mongoDatabase);
        return in.findAllDescBelowId(5792224709L)
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
        return writer.write(fullPublicMatchRepository.findAllAsc()
                .take(50_000)
                .flatMapSingle(factory::create))
                .doOnComplete(() -> log.info("Created training data"))
                .subscribeOn(Schedulers.io());
    }

    private Completable createTestData() {
        SampleWriter<INDArray> writer = new CsvSampleWriter(new File(TEST_DATA_PATH));
        return writer.write(fullPublicMatchRepository.findAllDesc()
                .take(10_000)
                .flatMapSingle(factory::create))
                .doOnComplete(() -> log.info("Created test data"))
                .subscribeOn(Schedulers.io());
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
