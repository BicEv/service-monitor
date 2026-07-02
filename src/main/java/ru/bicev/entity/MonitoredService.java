package ru.bicev.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a monitored service.
 * MonitoredService
 */
@Entity
@Table(name = "monitored_services")
public class MonitoredService extends PanacheEntityBase {

    /**
     * The unique identifier for the monitored service.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * The name of the monitored service.
     */
    public String name;

    /**
     * The URL of the monitored service.
     */
    public String url;

    /**
     * Interval in seconds between health checks for the monitored service. Default
     * value is 60 seconds.
     */
    public Integer checkIntervalSeconds = 60;

    /**
     * Expected HTTP status code for a successful health check. Default value is 200
     * (OK).
     */
    public Integer expectedStatusCode = 200;

    /**
     * Timestamp of the last health check performed for the monitored service.
     * Updates after every health check.
     */
    public LocalDateTime lastChecked;

    /**
     * Indicates whether the monitored service would be checked. Default value is
     * true. If set to false, the service will not be checked.
     */
    public Boolean active = true;

    /**
     * Indicates whether the monitored service is currently considered broken.
     * Default value is false. If set to true, the service is considered broken and
     * will be checked less frequently.
     */
    public Boolean broken = false;

}
