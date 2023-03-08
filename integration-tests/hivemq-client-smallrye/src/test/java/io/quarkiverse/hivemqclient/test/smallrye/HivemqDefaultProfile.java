package io.quarkiverse.hivemqclient.test.smallrye;

import java.util.Collections;
import java.util.List;

import io.quarkus.test.junit.QuarkusTestProfile;

public class HivemqDefaultProfile implements QuarkusTestProfile {
    @Override
    public List<TestResourceEntry> testResources() {
        return Collections.singletonList(new TestResourceEntry(HivemqResources.class));
    }
}
