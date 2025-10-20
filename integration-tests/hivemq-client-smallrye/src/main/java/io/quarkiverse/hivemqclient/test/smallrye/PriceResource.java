package io.quarkiverse.hivemqclient.test.smallrye;

import java.util.Random;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

/**
 * A simple resource retrieving the "in-memory" "my-data-stream" and sending the items to a server sent event.
 * Also provides on-demand price generation endpoints for testing purposes.
 */
@Path("/prices")
public class PriceResource {

    private static final Logger LOG = Logger.getLogger(PriceResource.class);
    private final Random random = new Random();

    @Inject
    @Channel("my-data-stream")
    Multi<Double> prices;

    @Inject
    @Channel("custom-topic-sink")
    Multi<String> customTopic;

    @Inject
    @Channel("topic-price")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<Integer> pricesEmitter;

    @Inject
    @Channel("custom-topic")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<Integer> customTopicEmitter;

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

    /**
     * Generate random prices on-demand and publish to the default prices topic.
     * This endpoint provides explicit control over price generation for testing purposes.
     *
     * @return JSON response with count of generated prices
     */
    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generatePrices() {
        int count = random.nextInt(4) + 2; // Generate 2-5 prices (minimum 2 to ensure tests receive multiple events)
        LOG.infof("Generating %d prices for topic-price", count);

        for (int i = 0; i < count; i++) {
            int price = random.nextInt(100);
            LOG.infof("Sending price to topic-price: %d", price);
            pricesEmitter.send(price);
        }

        return Response.ok()
                .entity(String.format("{\"generated\": %d, \"topic\": \"prices\"}", count))
                .build();
    }

    /**
     * Generate random prices on-demand and publish to the custom topic.
     * This endpoint provides explicit control over price generation for testing purposes.
     *
     * @return JSON response with count of generated prices
     */
    @POST
    @Path("/generate/custom")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateCustomPrices() {
        int count = random.nextInt(4) + 2; // Generate 2-5 prices (minimum 2 to ensure tests receive multiple events)
        LOG.infof("Generating %d prices for custom-topic", count);

        for (int i = 0; i < count; i++) {
            int price = random.nextInt(100);
            LOG.infof("Sending price to custom-topic: %d", price);
            customTopicEmitter.send(price);
        }

        return Response.ok()
                .entity(String.format("{\"generated\": %d, \"topic\": \"custom-topic\"}", count))
                .build();
    }
}
