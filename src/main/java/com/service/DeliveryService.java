package com.service;

import com.config.DeliveryConfig;
import com.model.DeliveryRecord;
import com.model.Device;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class DeliveryService {
    private final HttpClient http = HttpClient.newBuilder().build();

    @Inject
    DeliveryConfig deliveryConfig;

    @Transactional
    public void enqueueDelivery(Device device, String payload) {
        DeliveryRecord deliveryRecord = new DeliveryRecord();
        deliveryRecord.deviceId = device.id;
        deliveryRecord.callbackUrl = device.clientCallbackUrl;
        deliveryRecord.payload = payload;
        deliveryRecord.status = "PENDING";
        deliveryRecord.persist();
    }


    // Scheduler runs every 10s and tries to send pending deliveries
    @Scheduled(every = "10s")
    @Transactional
    public void processPending() {
        List<DeliveryRecord> list = DeliveryRecord.list("status", "PENDING");
        for (DeliveryRecord deliveryRecord : list) {
            trySend(deliveryRecord);
        }
    }


    private void trySend(DeliveryRecord r) {
        try {
            r.attempts++;
            r.lastAttemptAt = OffsetDateTime.now();
            r.persist();


            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(r.callbackUrl))
                    .timeout(Duration.ofMillis(deliveryConfig.clientTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(r.payload))
                    .build();


            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());


            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                r.status = "SENT";
                r.persist();
            } else {
                handleFailedAttempt(r);
            }
        } catch (Exception e) {
            handleFailedAttempt(r);
        }
    }


    private void handleFailedAttempt(DeliveryRecord r) {
        if (r.attempts >= deliveryConfig.retryMaxAttempts()) {
            r.status = "FAILED";
            r.persist();
        } else {
            // leave as PENDING so next scheduled run will retry
            r.persist();
        }
    }
}
