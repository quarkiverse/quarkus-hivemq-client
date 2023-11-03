package io.quarkiverse.hivemqclient.test.smallrye.resources;

import java.util.Collections;
import java.util.List;

import io.quarkus.test.junit.QuarkusTestProfile;

public class HivemqProfiles {
    public static class NoAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(NoAuthResources.class));
        }
    }

    public static class BasicAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(RbacAuthResources.class));
        }
    }
}
