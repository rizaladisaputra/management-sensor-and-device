package com.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.OffsetDateTime;

@Entity
public class Device extends PanacheEntity {
    public String name;
    public String clientCallbackUrl; // where to send monitoring data (HTTP/S)
    public boolean active = true;
    public OffsetDateTime createdAt = OffsetDateTime.now();
}
