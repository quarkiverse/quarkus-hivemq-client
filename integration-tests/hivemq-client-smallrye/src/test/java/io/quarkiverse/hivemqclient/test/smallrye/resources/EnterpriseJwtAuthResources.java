package io.quarkiverse.hivemqclient.test.smallrye.resources;

import static io.quarkiverse.hivemqclient.test.smallrye.utils.JwtUtils.generateRsaKey;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.MountableFile;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import io.quarkiverse.hivemqclient.test.smallrye.utils.JwtUtils;
import io.vertx.core.Vertx;

public class EnterpriseJwtAuthResources extends HiveMQEnterpriseEdition {
    private static final String EXTENSIONS_PATH = "/opt/hivemq/extensions/hivemq-enterprise-security-extension";
    private final static String DEFAULT_USERNAME = "JohnDoe";
    private final static int JWT_EXP_DAYS = 3;
    public static final int WIREMOCK_INTERNAL_PORT = 8080;
    public static final String BROKER_CONFIG_XML = "src/test/resources/jwtAuthConfig.xml";
    private RSAKey rsaKey;
    private GenericContainer<?> wireMockContainer;
    private Network network = Network.newNetwork();

    @Override
    public Map<String, String> start() {
        rsaKey = generateRsaKey();
        startWireMockContainer();
        hivemqContainer.withFileSystemBind("target/test-classes/hivemq-enterprise-security-extension/", EXTENSIONS_PATH,
                BindMode.READ_WRITE);
        hivemqContainer.withNetwork(network);
        hivemqContainer.withHiveMQConfig(MountableFile.forHostPath(BROKER_CONFIG_XML));

        String hiveId = UUID.randomUUID().toString();
        JwtUtils jwtUtils = new JwtUtils(JWSAlgorithm.RS256, rsaKey, Vertx.vertx());
        String jwt = jwtUtils.generateJwt(hiveId, JWT_EXP_DAYS);

        Map<String, String> config = super.start();
        config.put("mp.messaging.outgoing.topic-price.username", DEFAULT_USERNAME);
        config.put("mp.messaging.outgoing.topic-price.password", jwt);
        config.put("mp.messaging.incoming.prices.username", DEFAULT_USERNAME);
        config.put("mp.messaging.incoming.prices.password", jwt);
        return config;
    }

    @Override
    public void stop() {
        if (wireMockContainer != null) {
            wireMockContainer.stop();
        }
        super.stop();
    }

    private void startWireMockContainer() {
        wireMockContainer = new GenericContainer<>("wiremock/wiremock")
                .withExposedPorts(WIREMOCK_INTERNAL_PORT)
                .withNetwork(network)
                .withNetworkAliases("jwt-wiremock-container");

        wireMockContainer.start();
        int wireMockPort = wireMockContainer.getMappedPort(WIREMOCK_INTERNAL_PORT);
        WireMock.configureFor("localhost", wireMockPort);

        String jwksJson = new JWKSet(rsaKey.toPublicJWK()).toString();

        WireMock.stubFor(WireMock.get("/.well-known/jwks.json")
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jwksJson)));

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        return WireMock.get("/.well-known/jwks.json").build().getResponse().getStatus() == 200;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

}
