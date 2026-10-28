package service;

import model.SecurityIncident;
import repository.IncidentRepository;
import observer.IncidentObserver;
import java.util.ArrayList;
import java.util.List;

public class IncidentManager {
    private final IncidentRepository repository;
    private final List<IncidentObserver> observers = new ArrayList<>();
    private final AuditLogger logger = new AuditLogger();

    public IncidentManager(IncidentRepository repository) {
        this.repository = repository;
    }

    public void addObserver(IncidentObserver observer) {
        observers.add(observer);
    }

    public void createIncident(SecurityIncident incident) {
        repository.save(incident);
        logger.log("Yeni olay oluşturuldu: " + incident.getId());
        observers.forEach(o -> o.onIncidentCreated(incident));
    }

    public List<SecurityIncident> getAllIncidents() {
        return repository.findAll();
    }
}
