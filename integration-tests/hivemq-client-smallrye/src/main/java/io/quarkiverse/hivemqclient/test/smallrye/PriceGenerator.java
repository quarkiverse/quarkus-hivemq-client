package io.quarkiverse.hivemqclient.test.smallrye;

import java.time.Duration;
import java.util.Random;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;

import javax.enterprise.context.ApplicationScoped;

/**
 * A bean producing random prices every second.
 * The prices are written to a MQTT topic (prices). The MQTT configuration is specified in the application configuration.
 */
@ApplicationScoped
public class PriceGenerator {

    private static final Logger LOG = Logger.getLogger(PriceGenerator.class);

    private Random random = new Random();

    @Outgoing("topic-price")
    public Multi<Integer> generate() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onOverflow().drop()
                .map(tick -> {
                    int price = random.nextInt(100);
                    LOG.infof("Sending price: %d", price);
                    return price;
                });
    }

}
