package ru.bicev.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a health check log entry for a monitored service.
 */
@Entity
@Table(name = "health_check_logs")
public class HealthCheckLog extends PanacheEntityBase {

    /**
     * Unique identifier for the health check log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * Reference to the monitored service associated with this health check log
     * entry.
     */
    @ManyToOne
    @JoinColumn(name = "service_id")
    public MonitoredService service;

    /**
     * Timestamp indicating when the health check was performed.
     */
    public LocalDateTime checkedAt;

    /**
     * HTTP status code returned by the health check request.
     */
    public Integer statusCode;

    /**
     * Response time in milliseconds for the health check request.
     */
    public Long responseTimeMs;

    /**
     * Indicates whether the health check was successful of failed.
     */
    public boolean isSuccess;

    /**
     * Reason for the failure if the health check was unsuccessful. This field may
     * be null if the health check was successful or no exception occurred during
     * the request.
     */
    public String failureReason;

}
