package ru.bicev.dto;

import java.time.LocalDateTime;

/**
 * 
 * ServiceFailureEvent DTO for representing a service failure event and to be
 * sent in Kafka.
 * Object of this class is used to send information about a service failure
 * event to Kafka.
 * 
 */
public class ServiceFailureEvent {

    /** Identifier of the failed service */
    public Long serviceId;
    /** Name of the failed service */
    public String serviceName;
    /** URL of the failed service */
    public String url;
    /** HTTP status code returned by the failed service */
    public Integer statusCode;
    /** Reason for the failure of the service */
    public String failureReason;
    /** Timestamp of when the failure event occured */
    public LocalDateTime timestamp;

    /** Default constructor. Required for a proper deserialization by Jackson */
    public ServiceFailureEvent() {
    }

    /**
     * Creates an object to be sent in Kafka.
     * @param serviceId         Identifier of the failed service
     * @param serviceName       Name of the failed service
     * @param url               URL of the failed service
     * @param statusCode        HTTP status code returned by the failed service
     * @param failureReason     Reason for the failure of the service
     * @param timestamp         Timestamp of when the failure event occured
     */
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
