package io.quarkiverse.hivemqclient.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.Config;

/**
 * Factory class for creating test fixtures and mock objects for MQTT testing.
 * Provides consistent test data creation across all test cases.
 */
public final class MqttTestFixtures {

    private MqttTestFixtures() {
        // Utility class
    }

    /**
     * Creates a mock Config with the specified properties.
     *
     * @param properties map of configuration properties
     * @return a mock Config instance
     */
    public static Config createMockConfig(Map<String, String> properties) {
        Config config = mock(Config.class);

        // Mock getValue for each property
        properties.forEach((key, value) -> {
            when(config.getOptionalValue(key, String.class))
                    .thenReturn(java.util.Optional.ofNullable(value));
            when(config.getValue(key, String.class)).thenReturn(value);
        });

        // Mock getPropertyNames
        when(config.getPropertyNames()).thenReturn(properties.keySet());

        return config;
    }

    /**
     * Creates a basic MQTT connector configuration with default values.
     *
     * @param channelName the channel name for this configuration
     * @return a map of configuration properties
     */
    public static Map<String, String> createBasicConnectorConfig(String channelName) {
        Map<String, String> config = new HashMap<>();
        config.put("channel-name", channelName);
        config.put("connector", "smallrye-mqtt-hivemq");
        config.put("host", "localhost");
        config.put("port", "1883");
        config.put("auto-generated-client-id", "true");
        config.put("auto-keep-alive", "true");
        config.put("ssl", "false");
        config.put("keep-alive-seconds", "30");
        config.put("max-inflight-queue", "10");
        config.put("auto-clean-session", "true");
        config.put("reconnect-attempts", "5");
        config.put("reconnect-interval-seconds", "1");
        config.put("connect-timeout-seconds", "60");
        config.put("trust-all", "false");
        return config;
    }

    /**
     * Creates an MQTT connector configuration with SSL enabled.
     *
     * @param channelName the channel name for this configuration
     * @return a map of configuration properties
     */
    public static Map<String, String> createSslConnectorConfig(String channelName) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("ssl", "true");
        config.put("port", "8883");
        config.put("ssl.truststore.type", "jks");
        config.put("ssl.truststore.location", "/path/to/truststore.jks");
        config.put("ssl.truststore.password", "truststore-password");
        return config;
    }

    /**
     * Creates an MQTT connector configuration for incoming (subscriber) channel.
     *
     * @param channelName the channel name for this configuration
     * @param topic the MQTT topic to subscribe to
     * @return a map of configuration properties
     */
    public static Map<String, String> createIncomingConfig(String channelName, String topic) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("topic", topic);
        config.put("qos", "1");
        config.put("broadcast", "false");
        config.put("failure-strategy", "fail");
        return config;
    }

    /**
     * Creates an MQTT connector configuration for outgoing (publisher) channel.
     *
     * @param channelName the channel name for this configuration
     * @param topic the MQTT topic to publish to
     * @return a map of configuration properties
     */
    public static Map<String, String> createOutgoingConfig(String channelName, String topic) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("topic", topic);
        config.put("qos", "1");
        config.put("merge", "false");
        return config;
    }

    /**
     * Creates an MQTT connector configuration with custom client ID.
     *
     * @param channelName the channel name for this configuration
     * @param clientId the custom client ID
     * @return a map of configuration properties
     */
    public static Map<String, String> createConfigWithClientId(String channelName, String clientId) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("client-id", clientId);
        config.put("auto-generated-client-id", "false");
        return config;
    }

    /**
     * Creates an MQTT connector configuration with authentication.
     *
     * @param channelName the channel name for this configuration
     * @param username the username for authentication
     * @param password the password for authentication
     * @return a map of configuration properties
     */
    public static Map<String, String> createAuthenticatedConfig(String channelName, String username, String password) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("username", username);
        config.put("password", password);
        return config;
    }

    /**
     * Creates an MQTT connector configuration with health check enabled.
     *
     * @param channelName the channel name for this configuration
     * @return a map of configuration properties
     */
    public static Map<String, String> createHealthCheckConfig(String channelName) {
        Map<String, String> config = createBasicConnectorConfig(channelName);
        config.put("check-topic-enabled", "true");
        config.put("check-topic-name", "$SYS/broker/uptime");
        config.put("readiness-timeout", "20000");
        config.put("liveness-timeout", "120000");
        return config;
    }
}
