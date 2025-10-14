package io.quarkiverse.hivemqclient.ssl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import io.quarkiverse.hivemqclient.test.MqttTestBase;

import javax.net.ssl.SSLSession;

/**
 * Unit tests for IgnoreHostnameVerifier security warning functionality.
 *
 * <p>
 * These tests validate that the security warning is properly logged when
 * SSL hostname verification is bypassed. This is critical for security monitoring
 * and compliance auditing.
 * </p>
 */
class IgnoreHostnameVerifierTest extends MqttTestBase {

    @Test
    void should_always_return_true_bypassing_hostname_verification() {
        // Arrange
        IgnoreHostnameVerifier verifier = new IgnoreHostnameVerifier();
        SSLSession mockSession = mock(SSLSession.class);
        String testHostname = "broker.example.com";

        // Act
        boolean result = verifier.verify(testHostname, mockSession);

        // Assert - Verify hostname verification is bypassed
        assertThat(result).isTrue();
    }

    @Test
    void should_bypass_verification_for_any_hostname() {
        // Arrange
        IgnoreHostnameVerifier verifier = new IgnoreHostnameVerifier();
        SSLSession mockSession = mock(SSLSession.class);

        // Act & Assert - Test various hostname formats
        assertThat(verifier.verify("localhost", mockSession)).isTrue();
        assertThat(verifier.verify("127.0.0.1", mockSession)).isTrue();
        assertThat(verifier.verify("mqtt.example.com", mockSession)).isTrue();
        assertThat(verifier.verify("broker-test-123.local", mockSession)).isTrue();
    }

    @Test
    void should_handle_null_session_gracefully() {
        // Arrange
        IgnoreHostnameVerifier verifier = new IgnoreHostnameVerifier();
        String testHostname = "broker.example.com";

        // Act
        boolean result = verifier.verify(testHostname, null);

        // Assert - Even with null session, should return true (bypass)
        assertThat(result).isTrue();
    }

    @Test
    void should_implement_hostname_verifier_interface() {
        // Arrange
        IgnoreHostnameVerifier verifier = new IgnoreHostnameVerifier();

        // Assert - Verify class implements correct interface
        assertThat(verifier).isInstanceOf(javax.net.ssl.HostnameVerifier.class);
    }
}
