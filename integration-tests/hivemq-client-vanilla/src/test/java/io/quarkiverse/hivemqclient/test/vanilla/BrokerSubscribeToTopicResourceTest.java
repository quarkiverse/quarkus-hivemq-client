package io.quarkiverse.hivemqclient.test.vanilla;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@TestProfile(HivemqDefaultProfile.class)
@QuarkusTest
class BrokerSubscribeToTopicResourceTest extends CommonsTestSuite {
    private final String message = "helloWorld!";
    private final String topicName = "hello";

    @TestHTTPResource("/mqtt/")
    private URI brokerSubscribeToTopicResource;

    @Test
    void verifyServerSentEventSubscription() throws InterruptedException {

        // create a client for `BrokerSubscribeToTopicResources` and collect the consumed resources in a list
        WebTarget target = ClientBuilder.newClient()
                .target(brokerSubscribeToTopicResource + DEFAULT_BROKER_NAME + "/subscribe/hello");
        List<String> received = new CopyOnWriteArrayList<>();
        SseEventSource source = SseEventSource.target(target).build();
        source.register(inboundSseEvent -> received.add(inboundSseEvent.readData()));

        // in a separate thread, feed the `BrokerPushToTopicResource`
        ExecutorService msgSender = startSendingMsg();
        source.open();
        // check if, after at most 5 seconds, we have at least 'expectedMsgAmount' items collected, and they are what we expect
        await().atMost(30, SECONDS).until(() -> received.size() >= 2);
        assertThat(received, Matchers.hasItems("helloWorld!", "helloWorld!", "helloWorld!"));
        source.close();

        // shutdown the executor that is feeding the `BrokerPushToTopicResource`
        msgSender.shutdown();
        msgSender.awaitTermination(5, SECONDS);
    }

    private ExecutorService startSendingMsg() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (true) {
                try {
                    pushMessage(DEFAULT_BROKER_NAME, topicName, message, Response.Status.CREATED);
                } catch (Exception e) {
                    // Ignore errors when test is completed: Connection refused
                }
            }
        });
        return executorService;
    }
}
