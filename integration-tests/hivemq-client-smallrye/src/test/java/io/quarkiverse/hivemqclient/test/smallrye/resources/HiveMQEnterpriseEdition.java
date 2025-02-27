package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.time.Duration;

import org.slf4j.event.Level;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.utility.DockerImageName;

public class HiveMQEnterpriseEdition extends CommonResources {
    public static final int MQTT_PORT = 1883;

    protected final static HiveMQContainer hivemqContainer = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq4")
            .withTag("latest"))
            .waitingFor(new HostPortWaitStrategy()
                    .forPorts(MQTT_PORT)
                    .withStartupTimeout(Duration.ofMinutes(5)))
            .withLogLevel(Level.DEBUG);

    @Override
    protected HiveMQContainer getHiveMQContainer() {
        return hivemqContainer;
    }
}
