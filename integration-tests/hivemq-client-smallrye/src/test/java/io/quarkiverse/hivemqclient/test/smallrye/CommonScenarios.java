package io.quarkiverse.hivemqclient.test.smallrye;

import io.quarkus.runtime.Quarkus;
import io.quarkus.test.common.http.TestHTTPResource;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSource;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonScenarios {
    private static final int TIMEOUT_SEC = 10;
    private static final Logger LOG = Logger.getLogger(CommonScenarios.class);

    @TestHTTPResource("prices/stream")
    URI pricesUrl;

    @TestHTTPResource("prices/topic")
    URI customTopicUrl;

    @AfterAll
    public static void shutdown() {
        LOG.info("Stopping Quarkus application...");
        Quarkus.asyncExit();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void shouldGetHello() {
        given()
                .when().get("/prices")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

    @Test
    public void shouldGetStreamOfPrices() {
        AtomicInteger totalAmountReceived = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(pricesUrl);
        CountDownLatch expectedAmount = new CountDownLatch(2);

        SseEventSource source = SseEventSource.target(target).build();
        try {
            source.register(event -> {
                String value = event.readData(String.class, MediaType.APPLICATION_JSON_TYPE);
                LOG.infof("Received price: %s", value);
                totalAmountReceived.incrementAndGet();
            });
            source.open();
            expectedAmount.await(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            source.close();
            int received = totalAmountReceived.get();
            assertTrue(received > 1, "Expected more than 2 prices read from the source, got " + received);
        }
    }

    @Test
    public void testReceivedCustomTopic() {
        AtomicInteger totalAmountReceived = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(customTopicUrl);
        CountDownLatch expectedAmount = new CountDownLatch(2);
        SseEventSource source = SseEventSource.target(target).build();
        try {
            source.register(event -> {
                String value = event.readData(String.class, MediaType.APPLICATION_JSON_TYPE);
                LOG.infof("Received from custom topic value: %s", value);
                totalAmountReceived.incrementAndGet();
            });
            source.open();
            expectedAmount.await(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            source.close();
            int received = totalAmountReceived.get();
            assertTrue(received > 1, "Expected more than 2 prices read from the source, got " + received);
        }
    }

}
