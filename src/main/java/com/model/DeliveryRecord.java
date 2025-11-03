package com.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.OffsetDateTime;

@Entity
public class DeliveryRecord extends PanacheEntity {
    public Long deviceId;
    @Column(length = 2000)
    public String payload;
    public String callbackUrl;
    public int attempts = 0;
    public String status; // PENDING, SENT, FAILED
    public OffsetDateTime lastAttemptAt;
    public OffsetDateTime createdAt = OffsetDateTime.now();
}
