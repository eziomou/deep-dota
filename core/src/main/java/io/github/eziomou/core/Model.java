package io.github.eziomou.core;

import java.io.File;
import java.io.IOException;

public interface Model<N> {

    N getNetwork();

    void saveModel(File file) throws IOException;
}
