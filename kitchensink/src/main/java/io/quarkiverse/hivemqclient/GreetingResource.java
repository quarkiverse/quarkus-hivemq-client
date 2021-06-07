package io.quarkiverse.hivemqclient;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Path("/hello")
@Slf4j
public class GreetingResource {

    @Incoming("incoming-messages")
    public void creaOAggiornaTerminale(byte[] incoming) {
        log.info("new payload: {}", new String(incoming));
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
