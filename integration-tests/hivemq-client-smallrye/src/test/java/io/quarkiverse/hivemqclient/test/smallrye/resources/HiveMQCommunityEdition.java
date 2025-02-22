package io.quarkiverse.hivemqclient.test.smallrye.resources;

import static org.testcontainers.utility.DockerImageName.parse;

import org.slf4j.event.Level;
import org.testcontainers.hivemq.HiveMQContainer;

public class HiveMQCommunityEdition extends CommonResources {

    protected final static HiveMQContainer hivemqContainer = new HiveMQContainer(parse("hivemq/hivemq-ce").withTag("2024.2"))
            .withLogLevel(Level.DEBUG);

    @Override
    protected HiveMQContainer getHiveMQContainer() {
        return hivemqContainer;
    }
}
