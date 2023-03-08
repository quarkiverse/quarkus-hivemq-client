package io.quarkiverse.hivemqclient.test.vanilla.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestPath;

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;

import io.quarkiverse.hivemqclient.test.vanilla.dto.MessageDto;
import io.quarkiverse.hivemqclient.test.vanilla.dto.PublishMsgRespDto;
import io.quarkiverse.hivemqclient.test.vanilla.services.HiveProxy;
import io.smallrye.mutiny.Uni;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/mqtt/{brokerName}/push")
public class BrokerPushToTopicResources {

    private HiveProxy hiveProxy;

    BrokerPushToTopicResources(HiveProxy hiveProxy) {
        this.hiveProxy = hiveProxy;
    }

    @POST
    @Path("/{topicName}")
    public Uni<Response> pushMessage(@RestPath("brokerName") String brokerName, @RestPath("topicName") String topicName,
            MessageDto message) {
        return hiveProxy
                .pushSimpleMsg(brokerName, topicName, message.getValue())
                .onItem().transform(payload -> makePushMsgResponse(topicName, payload));
    }

    private static Response makePushMsgResponse(String topicName, Mqtt5PublishResult payload) {
        if (payload.getError().isPresent()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(payload.getError().get().getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).entity(new PublishMsgRespDto(topicName)).build();
    }
}
