package io.quarkiverse.hivemqclient.smallrye.reactive;

import static io.smallrye.reactive.messaging.mqtt.i18n.MqttExceptions.ex;
import static io.smallrye.reactive.messaging.mqtt.i18n.MqttLogging.log;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.mqtt.MqttFailStop;
import io.smallrye.reactive.messaging.mqtt.MqttFailureHandler;
import io.smallrye.reactive.messaging.mqtt.MqttIgnoreFailure;

import mutiny.zero.flow.adapters.AdaptersToFlow;

public class HiveMQMqttSource {

    private final Flow.Publisher<HiveMQReceivingMqttMessage> source;
    private final AtomicBoolean subscribed = new AtomicBoolean();
    private final Pattern pattern;

    public HiveMQMqttSource(HiveMQMqttConnectorIncomingConfiguration config) {
        String topic = config.getTopic().orElseGet(config::getChannel);
        int qos = config.getQos();
        boolean broadcast = config.getBroadcast();
        MqttFailureHandler.Strategy strategy = MqttFailureHandler.Strategy.from(config.getFailureStrategy());
        MqttFailureHandler onNack = createFailureHandler(strategy, config.getChannel());

        if (topic.contains("#") || topic.contains("+")) {
            String replace = topic.replace("+", "[^/]+")
                    .replace("#", ".+");
            pattern = Pattern.compile(replace);
        } else {
            pattern = null;
        }

        HiveMQClients.ClientHolder holder = HiveMQClients.getHolder(config);

        this.source = holder.connect()
                .onItem()
                .transformToMulti(client -> Multi.createFrom()
                        .publisher(AdaptersToFlow.publisher(client.subscribePublishesWith()
                                .topicFilter(topic).qos(MqttQos.fromCode(qos))
                                .applySubscribe().doOnSingle(subAck -> subscribed.set(true))))
                        .filter(m -> matches(topic, m))
                        .onItem().transform(x -> new HiveMQReceivingMqttMessage(x, onNack))
                        .stage(multi -> {
                            if (broadcast) {
                                return multi.broadcast().toAllSubscribers();
                            }
                            return multi;
                        })
                        .onCancellation().invoke(() -> subscribed.set(false))
                        .onFailure().invoke(log::unableToConnectToBroker));
    }

    private boolean matches(String topic, Mqtt3Publish m) {
        String topicName = m.getTopic().toString();
        if (pattern != null) {
            return pattern.matcher(topicName).matches();
        }
        return topicName.equals(topic);
    }

    private MqttFailureHandler createFailureHandler(MqttFailureHandler.Strategy strategy, String channel) {
        switch (strategy) {
            case IGNORE:
                return new MqttIgnoreFailure(channel);
            case FAIL:
                return new MqttFailStop(channel);
            default:
                throw ex.illegalArgumentUnknownStrategy(strategy.toString());
        }
    }

    public Flow.Publisher<HiveMQReceivingMqttMessage> getSource() {
        return source;
    }

    public boolean isSubscribed() {
        return subscribed.get();
    }
}
