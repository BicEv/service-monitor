package ru.bicev.repo;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import ru.bicev.entity.HealthCheckLog;

/**
 * Repository for managing HealthCheckLog entities.
 */
@ApplicationScoped
public class HealthCheckLogRepository implements PanacheRepository<HealthCheckLog> {

    /**
     * Finds health check logs by the associated service ID with pagination support.
     * @param serviceId     Identifier of monitored service
     * @param pageNum       Page number for pagination
     * @param pageSize      Page size for pagination
     * @return              List of HealthCheckLog entries for the specified service ID
     */
    public List<HealthCheckLog> findByServiceId(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1", Sort.ascending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    /**
     * Finds all health check logs with a success status failed, with pagination support.
     * @param pageNum       Page number for pagination 
     * @param pageSize      Page size for pagination
     * @return              List of HealthCheckLog entries with failed status
     */
    public List<HealthCheckLog> findAllFailures(int pageNum, int pageSize) {
        return find("isSuccess = false")
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    /**
     * Finds health check logs for a specific service that have failed, with pagination support.
     * @param serviceId     Identifier of monitored service
     * @param pageNum       Page number for pagination
     * @param pageSize      Page size for pagination
     * @return              List of HealthCheckLog entries for the specified service ID that have failed
     */
    public List<HealthCheckLog> findFailuresByService(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1 and isSuccess = false", Sort.descending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    /**
     * Finds the last health check logs with a limit on the number of entries returned, with pagination support.
     * @param limit         Limit on the number of entries to return
     * @param pageNum       Page number for pagination
     * @param pageSize      
     * @return              List of latest HealthCheckLog entries
     */
    public List<HealthCheckLog> findLastLogs(int limit, int pageNum, int pageSize) {
        return findAll(Sort.descending("checkedAt"))
                .page(Page.of(pageNum, Math.min(pageSize, limit)))
                .list();
    }

    /**
     * Finds health check logs by status code with pagination support.
     * @param statusCode    HTTP status code to filter logs
     * @param pageNum       Page number for pagination
     * @param pageSize      Page size for pagination
     * @return              List of HealthCheckLog entries with the specified status code
     */
    public List<HealthCheckLog> findByStatusCode(int statusCode, int pageNum, int pageSize) {
        return find("statusCode = ?1", Sort.descending("checkedAt"), statusCode)
                .page(Page.of(pageNum, pageSize))
                .list();
    }
}
