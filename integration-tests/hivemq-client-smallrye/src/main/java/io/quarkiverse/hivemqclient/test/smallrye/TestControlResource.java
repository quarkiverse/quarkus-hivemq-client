package io.quarkiverse.hivemqclient.test.smallrye;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

/**
 * REST endpoint for controlling test behavior, particularly for managing event generation
 * in native tests to prevent race conditions during shutdown.
 */
@Path("/test-control")
public class TestControlResource {

    private static final Logger LOG = Logger.getLogger(TestControlResource.class);

    @Inject
    PriceGenerator priceGenerator;

    /**
     * Stop all event generation. This should be called BEFORE initiating application shutdown
     * to prevent segfaults in native mode caused by MQTT messages being processed during shutdown.
     *
     * @return HTTP 200 with confirmation message
     */
    @POST
    @Path("/stop-events")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopEvents() {
        LOG.info("Test control API: Stopping event generation");
        priceGenerator.stopGeneratingEvents();
        return Response.ok()
                .entity("{\"status\": \"stopped\", \"message\": \"Event generation stopped successfully\"}")
                .build();
    }

    /**
     * Start event generation. This can be used to resume event generation after it has been stopped.
     *
     * @return HTTP 200 with confirmation message
     */
    @POST
    @Path("/start-events")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startEvents() {
        LOG.info("Test control API: Starting event generation");
        priceGenerator.startGeneratingEvents();
        return Response.ok()
                .entity("{\"status\": \"started\", \"message\": \"Event generation started successfully\"}")
                .build();
    }

    /**
     * Check the current status of event generation.
     *
     * @return HTTP 200 with status information
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        boolean isGenerating = priceGenerator.isGenerating();
        LOG.infof("Test control API: Current generation status: %s", isGenerating);
        return Response.ok()
                .entity(String.format("{\"generating\": %s}", isGenerating))
                .build();
    }
}
