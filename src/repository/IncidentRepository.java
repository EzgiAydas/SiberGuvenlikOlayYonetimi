package repository;

import model.SecurityIncident;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository {
    void save(SecurityIncident incident);
    Optional<SecurityIncident> findById(String id);
    List<SecurityIncident> findAll();
}
