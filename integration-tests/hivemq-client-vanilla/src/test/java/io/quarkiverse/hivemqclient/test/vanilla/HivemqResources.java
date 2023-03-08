package io.quarkiverse.hivemqclient.test.vanilla;

import static org.testcontainers.utility.DockerImageName.parse;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.hivemq.HiveMQContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class HivemqResources implements QuarkusTestResourceLifecycleManager {

    private final static HiveMQContainer hivemqContainer = new HiveMQContainer(parse("hivemq/hivemq-ce"));

    @Override
    public Map<String, String> start() {
        hivemqContainer.start();
        Map<String, String> config = new HashMap<>();
        config.put("hivemq.cluster.host", hivemqContainer.getHost());
        config.put("hivemq.cluster.port", "" + hivemqContainer.getMqttPort());

        return config;
    }

    @Override
    public void stop() {
        hivemqContainer.stop();
    }
}
