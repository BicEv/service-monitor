package ru.bicev.controller;

import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.bicev.dto.MonitoredServiceDto;
import ru.bicev.entity.MonitoredService;
import ru.bicev.util.Mapper;

@Path("/api/v1/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitoredServiceResource {

    @GET
    public List<MonitoredServiceDto> getAllServices() {
        return Mapper.toServiceDtoList(MonitoredService.listAll());

    }

    @GET
    @Path("/{serviceId}")
    public Response getServiceById(@PathParam("serviceId") Long serviceId) {
        MonitoredService service = MonitoredService.findById(serviceId);
        if (service == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(Mapper.toServiceDto(service)).build();
        }
    }

    @GET
    @Path("/active")
    public List<MonitoredServiceDto> getActiveServices() {
        return Mapper.toServiceDtoList(MonitoredService.findActive());
    }

    @GET
    @Path("/inactive")
    public List<MonitoredServiceDto> getInactiveServices() {
        return Mapper.toServiceDtoList(MonitoredService.findInactive());
    }

    @GET
    @Path("/search")
    public List<MonitoredServiceDto> searchServices(@QueryParam("name") String name, @QueryParam("url") String url) {
        if (name != null && !name.isEmpty()) {
            return Mapper.toServiceDtoList(MonitoredService.findNameLike(name));
        } else if (url != null && !url.isEmpty()) {
            return Mapper.toServiceDtoList(MonitoredService.findUrlLike(url));
        } else {
            return List.of();
        }

    }

    @POST
    @Transactional
    public Response createService(@Valid MonitoredServiceDto createRequest) {
        var service = Mapper.toServiceEntity(createRequest);
        service.id = null;
        service.persist();

        return Response.status(Response.Status.CREATED)
                .entity(Mapper.toServiceDto(service))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteService(@PathParam("id") Long id) {
        boolean deleted = MonitoredService.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateService(@PathParam("id") Long id, @Valid MonitoredServiceDto updateRequest) {
        MonitoredService existing = MonitoredService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            Mapper.updateServiceEntityFromDto(existing, updateRequest);

            return Response.ok(Mapper.toServiceDto(existing)).build();
        }
    }

}
