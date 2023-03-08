package io.quarkiverse.hivemqclient.test.vanilla.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.quarkiverse.hivemqclient.test.vanilla.services.HiveProxy;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.core.eventbus.EventBus;

@Path("/mqtt/{brokerName}/subscribe")
public class BrokerSubscribeToTopicResources {

    private EventBus bus;
    private HiveProxy hiveProxy;

    BrokerSubscribeToTopicResources(EventBus bus, HiveProxy hiveProxy) {
        this.bus = bus;
        this.hiveProxy = hiveProxy;
    }

    @GET
    @Path("/{topicName}")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> subscribeToTopic(@RestPath("brokerName") String brokerName, @RestPath("topicName") String topicName) {
        return hiveProxy.subscribeTo(brokerName, topicName).toMulti()
                .flatMap(topic -> bus.<String> consumer(topic).bodyStream().toMulti());
    }

}
