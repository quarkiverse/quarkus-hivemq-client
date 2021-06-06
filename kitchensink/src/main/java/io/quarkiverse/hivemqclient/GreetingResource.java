package io.quarkiverse.hivemqclient;

import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/hello")
@Slf4j
public class GreetingResource {

    @Incoming("device-status")
    public void creaOAggiornaTerminale(byte[] incoming) throws IOException {
        log.info("new payload: {}",new String(incoming));
    }

    @Channel("comandi")
    @Inject
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<byte[]> comandi;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @SneakyThrows
    public String hello(@QueryParam("{idDispositivo") @DefaultValue("test-device") String idDispositivo) {

        final String topicNameSpace = String.format("prespe-clients-tests/%s/messaggi", idDispositivo);
        final var s = "Hello RESTEasy";

        comandi.send(MqttMessage.of(topicNameSpace, s.getBytes()));

        return s;
    }
}
