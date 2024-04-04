package io.quarkiverse.hivemqclient.test.smallrye;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;

public class CommonScenarios {
    private static final int TIMEOUT_SEC = 5;
    private static final Logger LOG = Logger.getLogger(CommonScenarios.class);

    @TestHTTPResource("prices/stream")
    URI pricesUrl;

    @TestHTTPResource("prices/topic")
    URI customTopicUrl;

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

        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                String value = event.readData(String.class, MediaType.APPLICATION_JSON_TYPE);
                LOG.infof("Received price: %s", value);
                totalAmountReceived.incrementAndGet();
            });
            source.open();
            expectedAmount.await(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
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

        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                String value = event.readData(String.class, MediaType.APPLICATION_JSON_TYPE);
                LOG.infof("Received from custom topic value: %s", value);
                totalAmountReceived.incrementAndGet();
            });
            source.open();
            expectedAmount.await(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            int received = totalAmountReceived.get();
            assertTrue(received > 1, "Expected more than 2 prices read from the source, got " + received);
        }
    }

}
