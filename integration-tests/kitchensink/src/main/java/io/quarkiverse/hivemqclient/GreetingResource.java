package io.quarkiverse.hivemqclient;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.mqtt.MqttMessage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Path("/hello")
@Slf4j
public class GreetingResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingResource.class);

    @Incoming("incoming-messages")
    public void creaOAggiornaTerminale(byte[] incoming) {
        LOGGER.info("new payload: {}", new String(incoming));
    }

    @Channel("commands")
    @Inject
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<byte[]> commands;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @SneakyThrows
    public String hello(@QueryParam("{idDispositivo") @DefaultValue("test-device") String idDispositivo) {

        final String topicNameSpace = String.format("kitchensink/outgoing-messages/%s", idDispositivo);
        final var s = "Hello HiveMQ Client";

        commands.send(MqttMessage.of(topicNameSpace, s.getBytes()));

        return s;
    }
}
