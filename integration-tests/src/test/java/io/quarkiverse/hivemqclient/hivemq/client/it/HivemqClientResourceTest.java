package io.quarkiverse.hivemqclient.hivemq.client.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HivemqClientResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hivemq-client")
                .then()
                .statusCode(200)
                .body(is("Hello hivemq-client"));
    }
}
