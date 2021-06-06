package io.quarkiverse.hivemqclient.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExcludeDependencyBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;

class HiveMQClientProcessor {

    private static final String FEATURE = "hivemq-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void build(BuildProducer<RuntimeInitializedPackageBuildItem> initializedPackageBuildItemBuildProducer) {
        initializedPackageBuildItemBuildProducer.produce(new RuntimeInitializedPackageBuildItem("com.hivemq"));
    }

    @BuildStep
    public void ignore(BuildProducer<ExcludeDependencyBuildItem> excludeDependencyBuildItemBuildProducer) {
        excludeDependencyBuildItemBuildProducer.produce(new ExcludeDependencyBuildItem("com.hivemq", "hivemq-mqtt-client"));
    }
}
