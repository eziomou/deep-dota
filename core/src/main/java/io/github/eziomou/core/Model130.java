package io.github.eziomou.core;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public final class Model130 implements Model<MultiLayerNetwork> {

    private static final int INPUTS_NUMBER = 130;
    private static final int OUTPUTS_NUMBER = 2;
    private static final double LEARNING_RATE = 1e-3;
    private static final double L2_RATIO = 1e-4;

    private final MultiLayerNetwork network;

    public Model130() {
        this.network = new MultiLayerNetwork(getConfiguration());
        this.network.init();
    }

    public Model130(File file) throws IOException {
        this.network = MultiLayerNetwork.load(file, true);
    }

    @Override
    public MultiLayerNetwork getNetwork() {
        return network;
    }

    private MultiLayerConfiguration getConfiguration() {
        return new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(LEARNING_RATE))
                .l2(L2_RATIO)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(INPUTS_NUMBER)
                        .nOut(20)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(20)
                        .nOut(20)
                        .build())
                .layer(new OutputLayer.Builder()
                        .nIn(20)
                        .nOut(OUTPUTS_NUMBER)
                        .activation(Activation.SIGMOID)
                        .lossFunction(LossFunctions.LossFunction.XENT)
                        .build())
                .build();
    }

    @Override
    public void saveModel(File file) throws IOException {
        network.save(file, true);
    }
}
