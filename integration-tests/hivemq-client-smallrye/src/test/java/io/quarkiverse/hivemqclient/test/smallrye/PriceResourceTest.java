package io.quarkiverse.hivemqclient.test.smallrye;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

@TestProfile(HivemqDefaultProfile.class)
@QuarkusTest
public class PriceResourceTest {

    private static final Logger LOG = Logger.getLogger(PriceResourceTest.class);

    @TestHTTPResource("prices/stream")
    URI pricesUrl;

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
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(pricesUrl);

        AtomicInteger priceCount = new AtomicInteger();

        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                Double value = event.readData(Double.class);
                LOG.infof("Received price: %f", value);
                priceCount.incrementAndGet();
            });
            source.open();
            Thread.sleep(15 * 1000L);
        } catch (InterruptedException ignored) {
        }

        int count = priceCount.get();
        assertTrue(count > 1, "Expected more than 2 prices read from the source, got " + count);
    }

}
