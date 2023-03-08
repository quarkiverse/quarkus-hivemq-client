package io.quarkiverse.hivemqclient.test.vanilla;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@TestProfile(HivemqDefaultProfile.class)
@QuarkusTest
public class BrokerPushToTopicResourcesTest extends CommonsTestSuite {

    @Test
    public void verifyMessageIsPushed() {
        final String message = "helloWorld!";
        final String topicName = "hello";

        pushMessage(DEFAULT_BROKER_NAME, topicName, message, Response.Status.CREATED);
    }

    @Test
    public void verifyMassivePush() {
        final int amountOfEvents = 1000;
        final String message = "helloWorld!";
        final String topicName = "hello";

        for (int i = 0; i < amountOfEvents; i++) {
            pushMessage(DEFAULT_BROKER_NAME, topicName, message, Response.Status.CREATED);
        }
    }
}
