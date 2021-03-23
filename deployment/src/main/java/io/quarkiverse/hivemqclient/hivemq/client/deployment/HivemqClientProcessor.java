package io.quarkiverse.hivemqclient.hivemq.client.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class HivemqClientProcessor {

    private static final String FEATURE = "hivemq-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
