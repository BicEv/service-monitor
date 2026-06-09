package ru.bicev.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "health_check_logs")
public class HealthCheckLog extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "service_id")
    public MonitoredService service;

    public LocalDateTime checkedAt;
    public Integer statusCode;
    public Long responseTimeMs;
    public boolean isSuccess;
    public String failureReason;

    public static List<HealthCheckLog> findByServiceId(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1", Sort.ascending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public static List<HealthCheckLog> findAllFailures(int pageNum, int pageSize) {
        return find("isSuccess = false")
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public static List<HealthCheckLog> findFailuresByService(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1 and isSuccess = false", Sort.descending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public static List<HealthCheckLog> findLastLogs(int limit, int pageNum, int pageSize) {
        return findAll(Sort.descending("checkedAt"))
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public static List<HealthCheckLog> findByStatusCode(int statusCode, int pageNum, int pageSize) {
        return find("statusCode = ?1", Sort.descending("checkedAt"), statusCode)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

}
