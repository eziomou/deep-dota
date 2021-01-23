package io.github.eziomou.core;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public interface Model {

    int getInputsNumber();

    int getOutputsNumber();

    MultiLayerNetwork getNetwork();
}
