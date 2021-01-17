package io.github.eziomou.deepdota.webapi;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.eziomou.core.Model10X10;
import io.github.eziomou.core.StatsService;
import io.github.eziomou.data.FullMatchRepository;
import io.github.eziomou.deepdota.data.mongo.MongoFullMatchRepository;
import io.github.eziomou.predict.Model10X10Predictor;
import io.github.eziomou.predict.Predictor;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.StandardizeSerializerStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "prediction")
class PredictionConfig {

    private String modelPath;
    private String statisticsPath;

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getStatisticsPath() {
        return statisticsPath;
    }

    public void setStatisticsPath(String statisticsPath) {
        this.statisticsPath = statisticsPath;
    }

    @Bean
    Model10X10 model(PredictionConfig config) throws IOException {
        return new Model10X10(new File(config.modelPath));
    }

    @Bean
    MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    MongoDatabase mongoDatabase(MongoClient client) {
        return client.getDatabase("dota");
    }

    @Bean
    FullMatchRepository fullMatchRepository(MongoDatabase mongoDatabase) {
        return new MongoFullMatchRepository(mongoDatabase);
    }

    @Bean
    Predictor predictor(Model10X10 model, DataNormalization normalizer, StatsService statsService) {
        return new Model10X10Predictor(model, normalizer, statsService);
    }

    @Bean
    StatsService statsService(FullMatchRepository fullMatchRepository) {
        return new StatsService(fullMatchRepository.findAllDesc().take(50_000));
    }

    @Bean
    DataNormalization normalizer() throws Exception {
        NormalizerSerializer serializer = new NormalizerSerializer()
                .addStrategy(new StandardizeSerializerStrategy());
        return serializer.restore(new File(statisticsPath));
    }
}
