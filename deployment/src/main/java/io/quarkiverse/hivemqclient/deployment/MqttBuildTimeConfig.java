package io.quarkiverse.hivemqclient.deployment;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "hivemq")
public interface MqttBuildTimeConfig {

    /**
     * Configuration for DevServices. DevServices allows Quarkus to automatically start a MQTT broker in dev and test mode.
     */
    MqttDevServicesBuildTimeConfig devservices();
}
