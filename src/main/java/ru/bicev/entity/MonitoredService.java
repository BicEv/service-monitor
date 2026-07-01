package ru.bicev.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitored_services")
public class MonitoredService extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    public String url;

    public Integer checkIntervalSeconds = 60;

    public Integer expectedStatusCode = 200;

    public LocalDateTime lastChecked;

    public Boolean active = true;

}
