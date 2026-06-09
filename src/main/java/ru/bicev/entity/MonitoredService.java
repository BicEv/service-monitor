package ru.bicev.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitored_services")
public class MonitoredService extends PanacheEntity {
    public String name;
    public String url;
    public Integer checkIntervalSeconds;
    public Integer expectedStatusCode;
    public boolean active;

}
