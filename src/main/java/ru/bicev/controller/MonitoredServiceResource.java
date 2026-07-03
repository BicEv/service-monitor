package ru.bicev.controller;

import java.util.List;

import jakarta.inject.Inject;
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
import ru.bicev.repo.MonitoredServiceRepository;
import ru.bicev.util.Mapper;

/**
 * Contoller that provides API for access monitored service.
 * Base URL of controller is "/api/v1/services"
 */
@Path("/api/v1/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitoredServiceResource {

    /** Bean of MonitoredServiceRepository repository */
    @Inject
    private MonitoredServiceRepository serviceRepository;

    /**
     * Finds all monitored services
     * 
     * @return List of JSON serialized MonitoredServiceDto DTOs
     */
    @GET
    public List<MonitoredServiceDto> getAllServices() {
        return Mapper.toServiceDtoList(serviceRepository.listAll());

    }

    /**
     * Finds a monitored service with the specified ID
     * 
     * @param serviceId ID of the monitored service
     * @return JSON serialized MonitoredServiceDto or HTTP status NOT_FOUND if
     *         service with provided ID is not found
     */
    @GET
    @Path("/{serviceId}")
    public Response getServiceById(@PathParam("serviceId") Long serviceId) {
        MonitoredService service = serviceRepository.findById(serviceId);
        if (service == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(Mapper.toServiceDto(service)).build();
        }
    }

    /**
     * Finds all active monitored services
     * 
     * @return List of all active JSON serialized MonitoredServiceDto DTOs
     */
    @GET
    @Path("/active")
    public List<MonitoredServiceDto> getActiveServices() {
        return Mapper.toServiceDtoList(serviceRepository.findActive());
    }

    /**
     * Finds all inactive monitored services
     * 
     * @return List of all inactive JSON serialized MonitoredServiceDto DTOs
     */
    @GET
    @Path("/inactive")
    public List<MonitoredServiceDto> getInactiveServices() {
        return Mapper.toServiceDtoList(serviceRepository.findInactive());
    }

    /**
     * Finds all broken monitored services
     * 
     * @return List of all broken JSON serialized MonitoredServiceDto DTOs
     */
    @GET
    @Path("/broken")
    public List<MonitoredServiceDto> getBrokenServices() {
        return Mapper.toServiceDtoList(serviceRepository.findBrokenServices());
    }

    /**
     * Finds a monitored service with given paramethers (name/URL)
     * 
     * @param name Name to search for
     * @param url  URL to search for
     * @return List of JSON serialized MonitoredServiceDto DTOs with similar name or
     *         URL or empty list of no such services were found
     */
    @GET
    @Path("/search")
    public List<MonitoredServiceDto> searchServices(@QueryParam("name") String name, @QueryParam("url") String url) {
        var services = serviceRepository.search(name, url);
        return Mapper.toServiceDtoList(services);

    }

    /**
     * Creates new monitored service with the given request
     * 
     * @param createRequest MonitoredServiceDto request
     * @return JSON serialized MonitoredServiceDto corresponding created monitored
     *         service
     */
    @POST
    @Transactional
    public Response createService(@Valid MonitoredServiceDto createRequest) {
        var service = Mapper.toServiceEntity(createRequest);
        serviceRepository.persist(service);

        return Response.status(Response.Status.CREATED)
                .entity(Mapper.toServiceDto(service))
                .build();
    }

    /**
     * Deletes monitored service with the given ID
     * 
     * @param id ID of the monitored service to be deleted
     * @return HTTP status NO_CONTENT if service was deleted or NOT_FOUND if service
     *         with such ID does not exist
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteService(@PathParam("id") Long id) {
        boolean deleted = serviceRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Fully updates monitored service with given MonitoredServiceDto update reques
     * 
     * @param id            ID of monitored service to be updated
     * @param updateRequest MonitoredServiceDto containing data to update
     * @return JSON serialized MonitoredServiceDto corresponding updated monitored
     *         service or HTTP status NOT_FOUND if monitored service with the given
     *         ID does not exist
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateService(@PathParam("id") Long id, @Valid MonitoredServiceDto updateRequest) {
        MonitoredService existing = serviceRepository.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            Mapper.updateServiceEntityFromDto(existing, updateRequest);

            return Response.ok(Mapper.toServiceDto(existing)).build();
        }
    }

}
