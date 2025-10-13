package io.quarkiverse.hivemqclient.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

/**
 * Base test class providing common setup and teardown for MQTT unit tests.
 * Includes Mockito initialization and common test utilities.
 */
public abstract class MqttTestBase {

    private AutoCloseable mocks;

    @BeforeEach
    void setUpBase() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDownBase() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    /**
     * Helper method to create a test channel name with a prefix.
     *
     * @param suffix the suffix to append to the test channel name
     * @return a formatted test channel name
     */
    protected String testChannelName(String suffix) {
        return "test-channel-" + suffix;
    }

    /**
     * Helper method to create a test topic name with a prefix.
     *
     * @param suffix the suffix to append to the test topic name
     * @return a formatted test topic name
     */
    protected String testTopicName(String suffix) {
        return "test/topic/" + suffix;
    }
}