package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.util.Map;

import org.testcontainers.utility.MountableFile;

// Creating the necessary certificates
// server Keystore(privateKey): keytool -genkey -keyalg RSA -alias hivemq -keystore hivemq.jks -storepass changeme -validity 3600 -keysize 2048
// keystore to a PEM file: keytool -exportcert -alias hivemq -keystore hivemq.jks -rfc -file server.pem
// client truststore(publicKey) file (Tls): keytool -importcert -alias hivemq -file server.pem -keystore server.jks -storepass changeme
// mtls
// client certificates: openssl req -x509 -newkey rsa:2048 -keyout mqtt-client-key.pem -out mqtt-client-cert.pem -days 360
// to pem: openssl x509 -outform der -in mqtt-client-cert.pem -out mqtt-client-cert.crt
// to jks (publicKey): keytool -import -file mqtt-client-cert.crt -alias client -keystore hivemq-trust-store.jks -storepass changeme
// client keystore (privateKey):
// openssl pkcs12 -export -in mqtt-client-cert.pem -inkey mqtt-client-key.pem -out client-keystore.p12 -name client-keystore
// keytool -importkeystore -srckeystore client-keystore.p12 -srcstoretype PKCS12 -destkeystore client-keystore.jks -deststoretype JKS
public class MtlsAuthResources extends HiveMQCommunityEdition {

    private final static String CONFIG_FILE_NAME = "/mtlsAuthConfig.xml";

    @Override
    public Map<String, String> start() {

        hivemqContainer.withHiveMQConfig(MountableFile.forClasspathResource(CONFIG_FILE_NAME));
        hivemqContainer.withCopyFileToContainer(MountableFile.forHostPath("src/test/resources/certs/hivemq.jks"),
                "/opt/hivemq/conf/hivemq.jks");
        hivemqContainer.withCopyFileToContainer(MountableFile.forHostPath("src/test/resources/certs/hivemq-trust-store.jks"),
                "/opt/hivemq/conf/hivemq-trust-store.jks");

        Map<String, String> config = super.start();

        config.put("mp.messaging.outgoing.topic-price.ssl", "true");
        config.put("mp.messaging.outgoing.topic-price.ssl.keystore.type", "jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.keystore.location", "src/test/resources/certs/client-keystore.jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.keystore.password", "changeme");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.type", "jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.password", "changeme");
        config.put("mp.messaging.outgoing.topic-price.ssl.truststore.hostVerifier", "false");

        config.put("mp.messaging.incoming.prices.ssl", "true");
        config.put("mp.messaging.incoming.prices.ssl.keystore.type", "jks");
        config.put("mp.messaging.incoming.prices.ssl.keystore.location", "src/test/resources/certs/client-keystore.jks");
        config.put("mp.messaging.incoming.prices.ssl.keystore.password", "changeme");
        config.put("mp.messaging.incoming.prices.ssl.truststore.type", "jks");
        config.put("mp.messaging.incoming.prices.ssl.truststore.location", "src/test/resources/certs/server.jks");
        config.put("mp.messaging.incoming.prices.ssl.truststore.password", "changeme");
        config.put("mp.messaging.incoming.prices.ssl.truststore.hostVerifier", "false");

        return config;
    }
}
