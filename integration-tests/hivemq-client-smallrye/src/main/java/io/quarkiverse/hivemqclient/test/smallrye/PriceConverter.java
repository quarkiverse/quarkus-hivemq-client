package io.quarkiverse.hivemqclient.test.smallrye;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Broadcast;

/**
 * A bean consuming data from the "prices" MQTT topic and applying some conversion.
 * The result is pushed to the "my-data-stream" stream which is an in-memory stream.
 */
@ApplicationScoped
public class PriceConverter {

    private static final Logger LOG = Logger.getLogger(PriceConverter.class);
    private static final double CONVERSION_RATE = 0.88;

    @Incoming("prices")
    @Outgoing("my-data-stream")
    @Broadcast
    public double process(byte[] priceRaw) {
        int priceInUsd = Integer.parseInt(new String(priceRaw));
        LOG.infof("Receiving price: %d ", priceInUsd);
        return priceInUsd * CONVERSION_RATE;
    }

}
