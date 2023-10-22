package io.quarkiverse.hivemqclient.test.smallrye;

import io.quarkiverse.hivemqclient.test.smallrye.resources.HivemqProfiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@TestProfile(HivemqProfiles.NoAuth.class)
@QuarkusTest
public class NoAuthPriceTest extends CommonScenarios {

}
