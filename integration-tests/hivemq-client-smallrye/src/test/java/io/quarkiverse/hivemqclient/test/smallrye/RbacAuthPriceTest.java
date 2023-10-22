package io.quarkiverse.hivemqclient.test.smallrye;

import io.quarkiverse.hivemqclient.test.smallrye.resources.HivemqProfiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@TestProfile(HivemqProfiles.BasicAuth.class)
@QuarkusTest
public class RbacAuthPriceTest extends CommonScenarios {

}
