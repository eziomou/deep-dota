package io.github.eziomou.deepdota.example;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.core.Model130;
import io.github.eziomou.core.Model130Trainer;
import io.github.eziomou.core.ModelTrainer;
import io.github.eziomou.data.*;
import io.github.eziomou.deepdota.data.mongo.MongoMatchRepository;
import io.github.eziomou.deepdota.data.mongo.MongoPlayerRepository;
import io.github.eziomou.deepdota.data.opendota.OpenDotaMatchRepository;
import io.github.eziomou.predict.Model130PredictionService;
import io.github.eziomou.predict.Prediction;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;

import java.io.File;
import java.util.stream.Collectors;

public class Application {

    private static final String MODEL_PATH = "example/src/main/resources/modelXY";
    private static final String DATA_PATH = "example/src/main/resources/dataXY.csv";
    private static final int SAMPLES_COUNT = 10_000;
    private static final int BATCH_SIZE = 512;
    private static final double TRAIN_TEST_RATIO = 0.7;
    private static final int EPOCHS_COUNT = 10;

    private final MongoClient mongoClient = MongoClients.create();
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("dota");

    public static void main(String[] args) {
        Application application = new Application();
        application.predict()
                .doOnSuccess(System.out::println)
                .blockingSubscribe();
    }

    public Completable prepareData() {
        MatchWriteRepository matches = new MongoMatchRepository(mongoDatabase);
        PlayerWriteRepository players = new MongoPlayerRepository(mongoDatabase);
        MatchWriteRepository csv = new Model130Repository(new File(DATA_PATH));
        return new OpenDotaMatchRepository()
                .findAllDesc()
                .take(SAMPLES_COUNT)
                .toList()
                .flatMapCompletable(ms -> Completable.mergeArray(
                        matches.saveAll(ms).subscribeOn(Schedulers.io()),
                        players.saveAll(ms.stream()
                                .flatMap(m -> m.getPlayers().stream())
                                .distinct()
                                .collect(Collectors.toList()))
                                .subscribeOn(Schedulers.io()),
                        csv.saveAll(ms).subscribeOn(Schedulers.io())
                ));
    }

    public Single<Model130> train() {
        return Single.fromCallable(() -> {
            UIServer uiServer = UIServer.getInstance();
            StatsStorage statsStorage = new InMemoryStatsStorage();
            uiServer.attach(statsStorage);

            Model130 model = new Model130();
            model.getNetwork().setListeners(new StatsListener(statsStorage));

            return model;
        }).flatMap(model -> {
            ModelTrainer<Model130> trainer = Model130Trainer.builder(new File(DATA_PATH))
                    .samplesCount(SAMPLES_COUNT)
                    .batchSize(BATCH_SIZE)
                    .trainTestRation(TRAIN_TEST_RATIO)
                    .epochsCount(EPOCHS_COUNT)
                    .build();
            return trainer.train(model).toSingle(() -> model);
        });
    }

    public Single<Prediction> predict() {
        return Single.fromCallable(() -> {
            Model130 model = new Model130(new File(MODEL_PATH));
            PlayerReadRepository players = new MongoPlayerRepository(mongoDatabase);
            return new Model130PredictionService(model, players);
        }).flatMap(service -> service.predict(Team.create(
                Player.create(104334048L).hero(Hero.UNDYING).build(),
                Player.create(256156323L).hero(Hero.RUBIC).build(),
                Player.create(176139572L).hero(Hero.GRIMSTROKE).build(),
                Player.create(133558180L).hero(Hero.PHANTOM_ASSASSIN).build(),
                Player.create(137855976L).hero(Hero.CENTAUR).build()
        ), Team.create(
                Player.create(93552791L).hero(Hero.ELDER_TITAN).build(),
                Player.create(195001460L).hero(Hero.TROLL_WARLORD).build(),
                Player.create(171981096L).hero(Hero.TEMPLAR_ASSASSIN).build(),
                Player.create(182993582L).hero(Hero.DARK_WILLOW).build(),
                Player.create(86723143L).hero(Hero.VOID_SPIRIT).build()
        )));
    }
}
