package com.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Sensor extends PanacheEntity {
    public String name;
    public String unit;
    public boolean active = true;
    @ManyToOne
    public Device device;
}
