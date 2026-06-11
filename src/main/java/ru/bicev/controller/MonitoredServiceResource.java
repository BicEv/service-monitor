package ru.bicev.controller;

import java.util.List;

import jakarta.transaction.Transactional;
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
import ru.bicev.entity.MonitoredService;

@Path("/api/v1/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitoredServiceResource {

    @GET
    public List<MonitoredService> getAllServices() {
        return MonitoredService.listAll();

    }

    @GET
    @Path("/active")
    public List<MonitoredService> getActiveServices() {
        return MonitoredService.findActive();
    }

    @GET
    @Path("/inactive")
    public List<MonitoredService> getInactiveServices() {
        return MonitoredService.findInactive();
    }

    @GET
    @Path("/search")
    public List<MonitoredService> searchServices(@QueryParam("name") String name, @QueryParam("url") String url) {
        if (name != null && !name.isEmpty()) {
            return MonitoredService.findNameLike(name);
        } else if (url != null && !url.isEmpty()) {
            return MonitoredService.findUrlLike(url);
        } else {
            return List.of();
        }

    }

    @POST
    @Transactional
    public Response createService(MonitoredService service) {
        service.id = null;
        service.persist();

        return Response.status(Response.Status.CREATED)
                .entity(service)
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
    public Response updateService(@PathParam("id") Long id, MonitoredService updatedService) {
        MonitoredService existing = MonitoredService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            existing.name = updatedService.name != null ? updatedService.name : existing.name;
            existing.url = updatedService.url != null ? updatedService.url : existing.url;
            existing.active = updatedService.active != null ? updatedService.active : existing.active;
            existing.expectedStatusCode = updatedService.expectedStatusCode != null ? updatedService.expectedStatusCode
                    : existing.expectedStatusCode;
            existing.checkIntervalSeconds = updatedService.checkIntervalSeconds != null
                    ? updatedService.checkIntervalSeconds
                    : existing.checkIntervalSeconds;

            return Response.ok(existing).build();
        }
    }

}
