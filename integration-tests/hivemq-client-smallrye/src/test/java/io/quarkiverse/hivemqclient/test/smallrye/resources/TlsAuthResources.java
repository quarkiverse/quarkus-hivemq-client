package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.util.Map;

import org.testcontainers.utility.MountableFile;

// Creating the necessary certificates
// server Keystore(privateKey): keytool -genkey -keyalg RSA -alias hivemq -keystore hivemq.jks -storepass changeme -validity 3600 -keysize 2048
// keystore to a PEM file: keytool -exportcert -alias hivemq -keystore hivemq.jks -rfc -file server.pem
// client truststore(publicKey) file(Tls): keytool -importcert -alias hivemq -file server.pem -keystore server.jks -storepass changeme
public class TlsAuthResources extends HiveMQCommunityEdition {

    private final static String CONFIG_FILE_NAME = "/tlsAuthConfig.xml";

    @Override
    public Map<String, String> start() {

        hivemqContainer.withHiveMQConfig(MountableFile.forClasspathResource(CONFIG_FILE_NAME));
        hivemqContainer.withCopyFileToContainer(MountableFile.forHostPath("src/test/resources/certs/hivemq.jks"),
                "/opt/hivemq/conf/hivemq.jks");

        Map<String, String> config = super.start();

        config.put("mp.messaging.outgoing.topic-price.ssl", "true");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.type", "jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.password", "changeme");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.hostVerifier", "false");

        config.put("mp.messaging.incoming.prices.ssl", "true");
        config.put("mp.messaging.incoming.prices.ssl.truststore.type", "jks");
        config.put("mp.messaging.incoming.prices.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.incoming.prices.ssl.truststore.password", "changeme");
        config.put("mp.messaging.incoming.prices.ssl.truststore.hostVerifier", "false");

        config.put("mp.messaging.outgoing.custom-topic.ssl", "true");
        config.put("mp.messaging.outgoing.custom-topic.ssl.truststore.type", "jks");
        config.put("mp.messaging.outgoing.custom-topic.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.outgoing.custom-topic.ssl.truststore.password", "changeme");
        config.put("mp.messaging.outgoing.custom-topic.ssl.truststore.hostVerifier", "false");

        config.put("mp.messaging.incoming.custom-topic-sink.ssl", "true");
        config.put("mp.messaging.incoming.custom-topic-sink.ssl.truststore.type", "jks");
        config.put("mp.messaging.incoming.custom-topic-sink.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.incoming.custom-topic-sink.ssl.truststore.password", "changeme");
        config.put("mp.messaging.incoming.custom-topic-sink.ssl.truststore.hostVerifier", "false");
        return config;
    }
}
