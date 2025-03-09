package io.quarkiverse.hivemqclient.smallrye.reactive;

import static io.smallrye.reactive.messaging.mqtt.i18n.MqttLogging.log;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

public class HiveMQPing {

    private final static int PING_TIMEOUT_SEC = 15;
    private final static String DEFAULT_ERROR_MSG = """
                Unable to reach HiveMQ server.
                Possible causes:
                - Incorrect host or port (check your broker address).
                - Authentication failure (verify username/password).
                - The MQTT broker is down or unreachable.
                - Network issues (firewall, VPN, or DNS misconfiguration).

                Please review your configuration and try again.
            """;

    private HiveMQPing() {
    }

    public static boolean isServerReachable(HiveMQClients.ClientHolder holder) {
        Mqtt3AsyncClient clientAsync = initializeMqttClient(holder);
        if (clientAsync == null || !HiveMQPing.checkConnection(clientAsync)) {
            log.warn(DEFAULT_ERROR_MSG);
            return false;
        }

        return true;
    }

    private static boolean checkConnection(Mqtt3AsyncClient client) {
        CompletableFuture<Boolean> pongReceived = new CompletableFuture<>();
        String pongTopic = "pong";
        try {
            if (!client.getConfig().getState().isConnected()) {
                client.connect()
                        .whenComplete((connAck, throwable) -> {
                            if (throwable != null) {
                                log.unableToConnectToBroker(throwable);
                            }
                        }).get(5, TimeUnit.SECONDS);
            }

            if (!client.getConfig().getState().isConnected()) {
                log.unableToConnectToBroker(new Exception("Client is not connected"));
                return false;
            }

            client.subscribeWith()
                    .topicFilter(pongTopic)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .callback(publish -> {
                        String payload = new String(publish.getPayloadAsBytes());
                        if ("ping".equals(payload)) {
                            pongReceived.complete(true);
                        }
                    })
                    .send()
                    .get(5, TimeUnit.SECONDS);

            Thread.sleep(500);

            client.publishWith()
                    .topic(pongTopic)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .payload("ping".getBytes())
                    .send()
                    .get(5, TimeUnit.SECONDS);

            return pongReceived.get(PING_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        } finally {
            client.unsubscribeWith()
                    .topicFilter(pongTopic)
                    .send();
        }
    }

    private static Mqtt3AsyncClient initializeMqttClient(HiveMQClients.ClientHolder holder) {
        try {
            return holder.connect()
                    .await().atMost(Duration.ofSeconds(10))
                    .toAsync();
        } catch (Exception e) {
            return null;
        }
    }

}
