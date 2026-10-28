package observer;

import model.SecurityIncident;

public interface IncidentObserver {
    void onIncidentCreated(SecurityIncident incident);
    void onStatusChanged(SecurityIncident incident);
}
