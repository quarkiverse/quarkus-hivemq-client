package io.quarkiverse.hivemqclient.smallrye.reactive;

import static io.smallrye.reactive.messaging.mqtt.i18n.MqttLogging.log;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3RxClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishResult;

import io.reactivex.Flowable;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.SendingMqttMessageMetadata;
import io.smallrye.reactive.messaging.providers.helpers.MultiUtils;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;

import mutiny.zero.flow.adapters.AdaptersToFlow;

public class HiveMQMqttSink {

    private final String topic;
    private final int qos;

    private final Flow.Subscriber<? extends Message<?>> sink;
    private final AtomicBoolean connected = new AtomicBoolean();

    public HiveMQMqttSink(Vertx vertx, HiveMQMqttConnectorOutgoingConfiguration config) {
        topic = config.getTopic().orElseGet(config::getChannel);
        qos = config.getQos();

        AtomicReference<Mqtt3RxClient> reference = new AtomicReference<>();

        sink = MultiUtils.via(msg -> msg.onSubscription()
                .call(() -> {

                    final Mqtt3RxClient client = reference.get();
                    if (client != null) {
                        if (client.getState().isConnected()) {
                            connected.set(true);
                        } else {
                            vertx.setPeriodic(100, id -> {
                                if (client.getState().isConnected()) {
                                    vertx.cancelTimer(id);
                                    connected.set(true);
                                }
                            });
                        }
                    }

                    return HiveMQClients.getConnectedClient(config)
                            .onItem().invoke(c -> {
                                reference.set(c);
                                connected.set(true);
                            });
                })
                .onItem().transformToUniAndConcatenate(m -> send(reference, m))
                .onCompletion().invoke(() -> {
                    Mqtt3RxClient c = reference.getAndSet(null);
                    if (c != null) {
                        c.toBlocking().disconnect();
                        connected.set(false);
                    }
                })
                .onFailure().invoke(e -> {
                    connected.set(false);
                    log.errorWhileSendingMessageToBroker(e);
                }));
    }

    private Uni<? extends Message<?>> send(AtomicReference<Mqtt3RxClient> reference, Message<?> msg) {
        Mqtt3RxClient client = reference.get();
        String actualTopicToBeUsed = this.topic;
        MqttQos actualQoS = MqttQos.fromCode(this.qos);
        boolean isRetain = false;

        Optional<SendingMqttMessageMetadata> sendingMqttMessageMetadata = msg.getMetadata()
                .get(SendingMqttMessageMetadata.class);

        if (sendingMqttMessageMetadata.isPresent()) {
            var mm = sendingMqttMessageMetadata.get();
            actualTopicToBeUsed = mm.getTopic() == null ? topic : mm.getTopic();
            actualQoS = MqttQos.fromCode(mm.getQosLevel() == null ? actualQoS.getCode() : mm.getQosLevel().value());
            isRetain = mm.isRetain();
        }

        if (actualTopicToBeUsed == null) {
            log.ignoringNoTopicSet();
            return Uni.createFrom().item(msg);
        }

        final Flowable<Mqtt3PublishResult> publish = client.publish(Flowable.just(Mqtt3Publish.builder()
                .topic(actualTopicToBeUsed)
                .qos(actualQoS)
                .payload(convert(msg.getPayload()))
                .retain(isRetain)
                .build()));

        return Uni.createFrom()
                .publisher(AdaptersToFlow.publisher(publish))
                .onItemOrFailure().transformToUni((s, f) -> {
                    if (f != null) {
                        return Uni.createFrom().completionStage(msg.nack(f).thenApply(x -> msg));
                    } else {
                        return Uni.createFrom().completionStage(msg.ack().thenApply(x -> msg));
                    }
                });
    }

    private ByteBuffer convert(Object payload) {
        final Buffer buffer = toBuffer(payload);
        return ByteBuffer.wrap(buffer.getBytes());
    }

    private Buffer toBuffer(Object payload) {
        if (payload instanceof JsonObject) {
            return new Buffer(((JsonObject) payload).toBuffer());
        }
        if (payload instanceof JsonArray) {
            return new Buffer(((JsonArray) payload).toBuffer());
        }
        if (payload instanceof String || payload.getClass().isPrimitive()) {
            return new Buffer(io.vertx.core.buffer.Buffer.buffer(payload.toString()));
        }
        if (payload instanceof byte[]) {
            return new Buffer(io.vertx.core.buffer.Buffer.buffer((byte[]) payload));
        }
        if (payload instanceof Buffer) {
            return (Buffer) payload;
        }
        if (payload instanceof io.vertx.core.buffer.Buffer) {
            return new Buffer((io.vertx.core.buffer.Buffer) payload);
        }
        // Convert to Json
        return new Buffer(Json.encodeToBuffer(payload));
    }

    public Flow.Subscriber<? extends Message<?>> getSink() {
        return sink;
    }

    public boolean isReady() {
        return connected.get();
    }
}
