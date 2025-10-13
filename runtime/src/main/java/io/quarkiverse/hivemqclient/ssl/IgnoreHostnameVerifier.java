package io.quarkiverse.hivemqclient.ssl;

import static io.smallrye.reactive.messaging.mqtt.i18n.MqttLogging.log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * HostnameVerifier implementation that disables SSL hostname verification.
 *
 * <p>
 * <strong>SECURITY WARNING:</strong> This verifier bypasses SSL hostname verification
 * and should ONLY be used in development or testing environments. Using this verifier
 * in production environments exposes your application to Man-in-the-Middle (MITM) attacks
 * where an attacker can intercept and modify all MQTT communication.
 * </p>
 *
 * <h3>Security Implications</h3>
 * <p>
 * SSL hostname verification ensures that the certificate presented by the MQTT broker
 * matches the hostname you intended to connect to. Disabling this verification allows an
 * attacker to present a valid certificate for a different domain and intercept your
 * encrypted traffic.
 * </p>
 *
 * <h3>When to Use This Verifier</h3>
 * <ul>
 * <li>Local development with self-signed certificates</li>
 * <li>Testing environments with non-production broker hostnames</li>
 * <li>Docker/Kubernetes environments with dynamic hostname allocation</li>
 * </ul>
 *
 * <h3>Production Alternative</h3>
 * <p>
 * For production systems, you MUST:
 * </p>
 * <ul>
 * <li>Use properly configured SSL certificates with matching hostnames</li>
 * <li>Keep hostname verification enabled (ssl.hostVerifier=true, the default)</li>
 * <li>Configure truststore with your broker's CA certificate</li>
 * </ul>
 *
 * <p>
 * This verifier is activated when the configuration property
 * {@code ssl.hostVerifier} is set to {@code false}.
 * </p>
 *
 * @see io.quarkiverse.hivemqclient.smallrye.reactive.HiveMQClients#setupSslConfig
 * @see javax.net.ssl.HostnameVerifier
 */
public class IgnoreHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        log.warn("SECURITY WARNING: SSL hostname verification disabled for '" + hostname + "'. " +
                "This should ONLY be used in development/testing environments. " +
                "Production systems MUST enable hostname verification to prevent MITM attacks.");
        return true;
    }
}
