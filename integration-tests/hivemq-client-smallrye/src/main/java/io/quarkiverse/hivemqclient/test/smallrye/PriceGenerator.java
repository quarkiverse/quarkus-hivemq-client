package io.quarkiverse.hivemqclient.test.smallrye;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;

/**
 * A bean producing random prices every second.
 * The prices are written to a MQTT topic (prices). The MQTT configuration is specified in the application configuration.
 */
@ApplicationScoped
public class PriceGenerator {

    private static final Logger LOG = Logger.getLogger(PriceGenerator.class);

    private final Random random = new Random();
    private ScheduledExecutorService scheduledExecutor;

    @Channel("custom-topic")
    @Inject
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<Integer> pricesEmitter;

    @PostConstruct
    public void createSender() {
        LOG.info("Create Sender for manual send method");

        scheduledExecutor = Executors.newScheduledThreadPool(1);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            int price = random.nextInt(100);
            LOG.infof("Sending to custom-topic price: %d", price);
            pricesEmitter.send(MqttMessage.of("custom-topic", price));
        }, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down PriceGenerator scheduled executor...");
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOG.warn("Executor did not terminate in time, forcing shutdown...");
                    scheduledExecutor.shutdownNow();
                    if (!scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                        LOG.error("Executor did not terminate after forced shutdown");
                    }
                }
                LOG.info("PriceGenerator scheduled executor shut down successfully");
            } catch (InterruptedException e) {
                LOG.warn("Interrupted during executor shutdown, forcing shutdown...");
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

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
