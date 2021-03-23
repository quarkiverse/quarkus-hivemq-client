package io.quarkiverse.hivemqclient.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HiveMQClientResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hivemq-client")
                .then()
                .statusCode(200)
                .body(is("Hello hivemq-client"));
    }
}
