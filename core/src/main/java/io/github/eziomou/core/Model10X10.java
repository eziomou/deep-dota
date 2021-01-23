package io.github.eziomou.core;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;

public final class Model10X10 implements Model {

    static final int INPUTS_NUMBER = 10 * 10;
    static final int OUTPUTS_NUMBER = 2;

    private final MultiLayerNetwork network;

    public Model10X10(double learningRate, double l2Ratio) {
        this.network = new MultiLayerNetwork(getConfiguration(learningRate, l2Ratio));
        this.network.init();
    }

    public Model10X10(MultiLayerNetwork network) {
        this.network = network;
    }

    @Override
    public int getInputsNumber() {
        return INPUTS_NUMBER;
    }

    @Override
    public int getOutputsNumber() {
        return OUTPUTS_NUMBER;
    }

    @Override
    public MultiLayerNetwork getNetwork() {
        return network;
    }

    private MultiLayerConfiguration getConfiguration(double learningRate, double l2Ratio) {
        return new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(learningRate))
                .l2(l2Ratio)
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
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();
    }
}
