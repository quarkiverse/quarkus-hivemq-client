package io.quarkiverse.hivemqclient.test.vanilla;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkiverse.hivemqclient.test.vanilla.dto.MessageDto;

public abstract class CommonsTestSuite {

    protected final static String DEFAULT_BROKER_NAME = "test";

    protected void pushMessage(final String brokerName, final String topic, final String msg,
            final Response.Status ExpectedStatusCode) {
        given()
                .when()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(new MessageDto(msg))
                .post("/mqtt/" + brokerName + "/push/" + topic)
                .then()
                .statusCode(ExpectedStatusCode.getStatusCode())
                .body("topic", equalTo(topic));
    }
}
