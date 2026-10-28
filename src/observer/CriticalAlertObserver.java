package observer;

import model.SecurityIncident;
import model.Severity;

public class CriticalAlertObserver implements IncidentObserver {
    @Override
    public void onIncidentCreated(SecurityIncident incident) {
        if (incident.getSeverity() == Severity.KRITIK) {
            System.out.println("[ACİL UYARI] Kritik Olay Oluşturuldu! ID: " + incident.getId());
        }
    }

    @Override
    public void onStatusChanged(SecurityIncident incident) {
        if (incident.getSeverity() == Severity.KRITIK) {
            System.out.println("[ACİL UYARI] Kritik Olay Durumu Değişti! ID: " + incident.getId() + " -> " + incident.getStatus());
        }
    }
}
