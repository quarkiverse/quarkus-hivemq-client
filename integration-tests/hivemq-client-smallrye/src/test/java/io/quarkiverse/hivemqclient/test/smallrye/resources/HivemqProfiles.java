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

    public static class RbacAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(RbacAuthResources.class));
        }
    }

    public static class TlsAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(TlsAuthResources.class));
        }
    }

    public static class MtlsAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(MtlsAuthResources.class));
        }
    }

    public static class JwtAuth implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(EnterpriseJwtAuthResources.class));
        }
    }
}
