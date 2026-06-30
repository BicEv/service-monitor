package ru.bicev.dto;

import java.time.LocalDateTime;

public class ServiceFailureEvent {

    public Long serviceId;
    public String serviceName;
    public String url;
    public Integer statusCode;
    public String failureReason;
    public LocalDateTime timestamp;

    public ServiceFailureEvent() {
    }

    public ServiceFailureEvent(Long serviceId, String serviceName, String url, Integer statusCode, String failureReason,
            LocalDateTime timestamp) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.url = url;
        this.statusCode = statusCode;
        this.failureReason = failureReason;
        this.timestamp = timestamp;
    }
}


