package io.github.eziomou.deepdota.webapi;

import io.github.eziomou.core.Advantage;
import io.github.eziomou.core.Model10X10;
import io.github.eziomou.predict.Model10X10Predictor;
import io.github.eziomou.predict.Predictor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
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
    private String synergyMatrixPath;
    private String counterMatrixPath;

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

    public String getSynergyMatrixPath() {
        return synergyMatrixPath;
    }

    public void setSynergyMatrixPath(String synergyMatrixPath) {
        this.synergyMatrixPath = synergyMatrixPath;
    }

    public String getCounterMatrixPath() {
        return counterMatrixPath;
    }

    public void setCounterMatrixPath(String counterMatrixPath) {
        this.counterMatrixPath = counterMatrixPath;
    }

    @Bean
    Model10X10 model(PredictionConfig config) throws IOException {
        return new Model10X10(MultiLayerNetwork.load(new File(config.modelPath), true));
    }

    @Bean
    Predictor predictor(Model10X10 model, DataNormalization normalizer, Advantage advantage) {
        return new Model10X10Predictor(model, normalizer, advantage);
    }

    @Bean
    Advantage statistics(PredictionConfig config) {
        return Advantage.load(config.getSynergyMatrixPath(), config.getCounterMatrixPath());
    }

    @Bean
    DataNormalization normalizer() throws Exception {
        NormalizerSerializer serializer = new NormalizerSerializer()
                .addStrategy(new StandardizeSerializerStrategy());
        return serializer.restore(new File(statisticsPath));
    }
}
