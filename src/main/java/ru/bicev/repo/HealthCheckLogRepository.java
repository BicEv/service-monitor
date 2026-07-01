package ru.bicev.repo;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import ru.bicev.entity.HealthCheckLog;

@ApplicationScoped
public class HealthCheckLogRepository implements PanacheRepository<HealthCheckLog> {

    public List<HealthCheckLog> findByServiceId(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1", Sort.ascending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public List<HealthCheckLog> findAllFailures(int pageNum, int pageSize) {
        return find("isSuccess = false")
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public List<HealthCheckLog> findFailuresByService(Long serviceId, int pageNum, int pageSize) {
        return find("service.id = ?1 and isSuccess = false", Sort.descending("checkedAt"), serviceId)
                .page(Page.of(pageNum, pageSize))
                .list();
    }

    public List<HealthCheckLog> findLastLogs(int limit, int pageNum, int pageSize) {
        return findAll(Sort.descending("checkedAt"))
                .page(Page.of(pageNum, Math.min(pageSize, limit)))
                .list();
    }

    public List<HealthCheckLog> findByStatusCode(int statusCode, int pageNum, int pageSize) {
        return find("statusCode = ?1", Sort.descending("checkedAt"), statusCode)
                .page(Page.of(pageNum, pageSize))
                .list();
    }
}
