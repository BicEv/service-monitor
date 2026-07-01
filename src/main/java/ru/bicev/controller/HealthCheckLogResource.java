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
import ru.bicev.dto.HealthCheckLogDto;
import ru.bicev.entity.HealthCheckLog;
import ru.bicev.util.Mapper;

@Path("/api/v1/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckLogResource {

    @GET
    @Path("/service/{serviceId}")
    public List<HealthCheckLogDto> getLogsByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(HealthCheckLog.findByServiceId(serviceId, pageNum, size));
    }

    @GET
    @Path("/failures")
    public List<HealthCheckLogDto> getAllFailures(
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(HealthCheckLog.findAllFailures(pageNum, size));
    }

    @GET
    @Path("/service/{serviceId}/failures")
    public List<HealthCheckLogDto> getFailuresByService(
            @PathParam("serviceId") Long serviceId,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(HealthCheckLog.findFailuresByService(serviceId, pageNum, size));
    }

    @GET
    @Path("/latest")
    public List<HealthCheckLogDto> getLatestLogs(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(HealthCheckLog.findLastLogs(limit, pageNum, size));
    }

    @GET
    @Path("/status")
    public List<HealthCheckLogDto> getLogsByStatusCode(
            @QueryParam("statusCode") @DefaultValue("500") int statusCode,
            @QueryParam("page") @DefaultValue("0") int pageNum,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Mapper.toHealthCheckLogDtoList(HealthCheckLog.findByStatusCode(statusCode, pageNum, size));
    }
}
