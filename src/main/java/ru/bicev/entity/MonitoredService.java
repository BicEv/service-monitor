package ru.bicev.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "monitored_services")
public class MonitoredService extends PanacheEntity {

    @NotBlank(message = "Name can not be empty")
    public String name;

    @NotBlank(message = "Url can not be empty")
    @URL(message = "Ivalid URL format")
    public String url;

    @Min(value = 1, message = "Interval must be greater or equal to 1 second")
    public Integer checkIntervalSeconds = 60;

    @Min(100)
    @Max(599)
    public Integer expectedStatusCode = 200;

    public LocalDateTime lastChecked;

    public Boolean active = true;

    public static List<MonitoredService> findActive() {
        return list("active", true);
    }

    public static List<MonitoredService> findReadyToCheck() {
        List<MonitoredService> active = find("active = true").list();

        LocalDateTime now = LocalDateTime.now();
        return active.stream()
                .filter(s -> s.lastChecked == null || s.lastChecked.plusSeconds(s.checkIntervalSeconds).isBefore(now))
                .toList();
    }

    public static List<MonitoredService> findInactive() {
        return list("active", false);
    }

    public static List<MonitoredService> findNameLike(String name) {
        return list("name ilike ?1", "%" + name + "%");
    }

    public static List<MonitoredService> findUrlLike(String url) {
        return list("url ilike ?1", "%" + url + "%");
    }

}
