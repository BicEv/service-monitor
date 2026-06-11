package ru.bicev.controller;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import ru.bicev.entity.HealthCheckLog;

@Path("/api/v1/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckLogResource {

    @GET
    @Path("/service/{serviceId}")
    public List<HealthCheckLog> getLogsByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return HealthCheckLog.findByServiceId(serviceId, pageNum, size);
    }

    @GET
    @Path("/failures")
    public List<HealthCheckLog> getAllFailures(
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return HealthCheckLog.findAllFailures(pageNum, size);
    }

    @GET
    @Path("/service/{serviceId}/failures")
    public List<HealthCheckLog> getFailuresByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return HealthCheckLog.findFailuresByService(serviceId, pageNum, size);
    }

    @GET
    @Path("/latest")
    public List<HealthCheckLog> getLatestLogs(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return HealthCheckLog.findLastLogs(limit, pageNum, size);
    }

    @GET
    @Path("/status")
    public List<HealthCheckLog> getLogsByStatusCode(
            @QueryParam("statusCode") @DefaultValue("500") int statusCode,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return HealthCheckLog.findByStatusCode(statusCode, pageNum, size);
    }
}
