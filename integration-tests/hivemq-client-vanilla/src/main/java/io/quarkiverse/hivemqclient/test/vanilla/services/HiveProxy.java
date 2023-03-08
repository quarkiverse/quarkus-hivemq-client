package io.quarkiverse.hivemqclient.test.vanilla.services;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;

@Singleton
public class HiveProxy {

    private EventBus bus;
    private String clusterHostName;
    private int clusterPort;
    private final Map<String, Mqtt5AsyncClient> connectionsPool = new ConcurrentHashMap<>();

    HiveProxy(
            EventBus bus,
            @ConfigProperty(name = "hivemq.cluster.host", defaultValue = "localhost") String clusterHostName,
            @ConfigProperty(name = "hivemq.cluster.port") int clusterPort) {

        this.bus = bus;
        this.clusterHostName = clusterHostName;
        this.clusterPort = clusterPort;
    }

    public Uni<Mqtt5PublishResult> pushSimpleMsg(final String brokerName, final String topic, final String msg) {
        return getMqttClient(brokerName)
                .flatMap(conn -> Uni.createFrom()
                        .completionStage(() -> conn.publishWith().topic(topic).payload(UTF_8.encode(msg)).send()));
    }

    public Uni<String> subscribeTo(final String brokerName, final String topic) {
        String internalTopicName = getInternalSubscriptionTopicName(brokerName, topic);
        return getMqttClient(brokerName)
                .map(conn -> {
                    conn.subscribeWith().topicFilter(topic).send();
                    conn.toAsync().publishes(ALL,
                            event -> bus.send(internalTopicName, UTF_8.decode(event.getPayload().get()).toString()));
                    return internalTopicName;
                });
    }

    private Uni<Mqtt5AsyncClient> getMqttClient(final String brokerName) {
        Mqtt5AsyncClient client = connectionsPool.get(brokerName);
        if (Objects.isNull(client)) {
            return newMQTTClient().invoke(asyncClient -> connectionsPool.put(brokerName, asyncClient));
        }

        return Uni.createFrom().item(client);
    }

    private Uni<Mqtt5AsyncClient> newMQTTClient() {
        Mqtt5AsyncClient asyncClient = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(clusterHostName)
                .serverPort(clusterPort)
                .buildAsync();

        Uni<Mqtt5ConnAck> ack = Uni.createFrom().completionStage(asyncClient::connect);
        return ack.map(completed -> asyncClient);
    }

    private String getInternalSubscriptionTopicName(final String brokerName, final String topic) {
        return brokerName + "_" + topic;
    }
}
