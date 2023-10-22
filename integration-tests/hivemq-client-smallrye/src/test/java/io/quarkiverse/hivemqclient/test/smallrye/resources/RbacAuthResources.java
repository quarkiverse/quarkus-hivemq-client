package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.util.Map;

import org.testcontainers.utility.MountableFile;

public class RbacAuthResources extends CommonResources {

    private final static String CONFIG_FILE_NAME = "/rbacAuthConfig.xml";
    private final static String DEFAULT_USERNAME = "test";
    private final static String DEFAULT_PASSWORD = "test";

    @Override
    public Map<String, String> start() {

        hivemqContainer.withHiveMQConfig(MountableFile.forClasspathResource(CONFIG_FILE_NAME));
        hivemqContainer.withExtension(MountableFile.forHostPath("src/test/resources/hivemq-file-rbac-extension"));

        hivemqContainer.withoutPrepackagedExtensions("hivemq-allow-all-extension");

        Map<String, String> config = super.start();

        config.put("mp.messaging.outgoing.topic-price.username", DEFAULT_USERNAME);
        config.put("mp.messaging.outgoing.topic-price.password", DEFAULT_PASSWORD);
        config.put("mp.messaging.incoming.prices.username", DEFAULT_USERNAME);
        config.put("mp.messaging.incoming.prices.password", DEFAULT_PASSWORD);
        return config;
    }
}
