package io.quarkiverse.hivemqclient.test.smallrye;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

/**
 * A simple resource retrieving the "in-memory" "my-data-stream" and sending the items to a server sent event.
 */
@Path("/prices")
public class PriceResource {

    @Inject
    @Channel("my-data-stream")
    Multi<Double> prices;

    @Inject
    @Channel("custom-topic-sink")
    Multi<String> customTopic;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/stream")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Double> stream() {
        return prices;
    }

    @GET
    @Path("/topic")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<String> customTopic() {
        return customTopic;
    }
}
