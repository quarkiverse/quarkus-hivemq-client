package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.hivemq.HiveMQContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public abstract class CommonResources implements QuarkusTestResourceLifecycleManager {

    protected abstract HiveMQContainer getHiveMQContainer();

    @Override
    public Map<String, String> start() {
        getHiveMQContainer().start();
        Map<String, String> config = new HashMap<>();
        config.put("mp.messaging.outgoing.topic-price.host", getHiveMQContainer().getHost());
        config.put("mp.messaging.outgoing.topic-price.port", "" + getHiveMQContainer().getMqttPort());
        config.put("mp.messaging.incoming.prices.host", getHiveMQContainer().getHost());
        config.put("mp.messaging.incoming.prices.port", "" + getHiveMQContainer().getMqttPort());

        // custom topic scenario
        config.put("mp.messaging.incoming.custom-topic-sink.host", getHiveMQContainer().getHost());
        config.put("mp.messaging.incoming.custom-topic-sink.port", "" + getHiveMQContainer().getMqttPort());
        config.put("mp.messaging.outgoing.custom-topic.host", getHiveMQContainer().getHost());
        config.put("mp.messaging.outgoing.custom-topic.port", "" + getHiveMQContainer().getMqttPort());
        return config;
    }

    @Override
    public void stop() {
        getHiveMQContainer().stop();
    }
}
