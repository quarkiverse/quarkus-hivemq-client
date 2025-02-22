package io.quarkiverse.hivemqclient.test.smallrye.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class JwtUtils {
    private static final Logger LOG = Logger.getLogger(JwtUtils.class);
    private static final String DEFAULT_AUDIENCE = "your-company-Api";
    private static final String DEFAULT_ISSUER = "HiveMQ-jwt-generator";
    private static final long TOKEN_EARLY_EPOCH_THRESHOLD_MINUTES = 1;
    private final JWSAlgorithm jwsAlgorithm;
    protected final RSAKey rsaJWK;
    private final Vertx vertx;

    public JwtUtils(JWSAlgorithm jwsAlgorithm, RSAKey rsaJWK, Vertx vertx) {
        this.jwsAlgorithm = jwsAlgorithm;
        this.rsaJWK = rsaJWK;
        this.vertx = vertx;
    }

    public String generateJwt(String hiveId, Integer lifetimeInDays) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(Long.toHexString(System.currentTimeMillis()))
                    .subject(hiveId)
                    .audience(DEFAULT_AUDIENCE)
                    .issuer(DEFAULT_ISSUER)
                    .issueTime(new Date())
                    .notBeforeTime(new Date(
                            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(TOKEN_EARLY_EPOCH_THRESHOLD_MINUTES)))
                    .expirationTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(lifetimeInDays)))
                    .claim("permissions", Set.of("READ", "WRITE", "ADMIN"))
                    .build();

            JWSHeader header = new JWSHeader.Builder(jwsAlgorithm).keyID(rsaJWK.getKeyID()).build();
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new RSASSASigner(rsaJWK));

            return Future.succeededFuture(signedJWT.serialize()).toCompletionStage().toCompletableFuture().get();
        } catch (Exception e) {
            LOG.error("Error generating JWT", e);
            fail(e.getMessage());
            return null;
        }
    }

    public static RSAKey generateRsaKey() {
        try {
            return new RSAKeyGenerator(2048)
                    .keyID("123")
                    .generate();
        } catch (JOSEException je) {
            throw new RuntimeException(je);
        }
    }
}
