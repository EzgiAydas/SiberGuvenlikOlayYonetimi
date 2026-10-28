package strategy;

import model.Analyst;
import model.SecurityIncident;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Greedy atama: müsait analistler arasından önce uzmanlığı uyanları,
 * onların içinden de iş yükü en az olanı seçer. Uzman yoksa müsait olanlar
 * arasından en az yüklü seçilir. Karmaşıklık O(n).
 * Olay zaten bir analiste atanmışsa mevcut atama korunur.
 */
public class WorkloadBasedAssigner implements AnalystAssigner {

    @Override
    public Optional<Analyst> assign(SecurityIncident incident, List<Analyst> analysts) {
        if (incident.getAssignedAnalyst() != null) {
            return Optional.of(incident.getAssignedAnalyst());
        }
        if (analysts == null || analysts.isEmpty()) {
            return Optional.empty();
        }

        Comparator<Analyst> byWorkload = Comparator.comparingInt(Analyst::getWorkload);
        String needed = incident.getRequiredExpertise();

        Optional<Analyst> chosen = analysts.stream()
                .filter(Analyst::isAvailable)
                .filter(a -> needed.equalsIgnoreCase(a.getExpertise()))
                .min(byWorkload);

        if (chosen.isEmpty()) {
            chosen = analysts.stream()
                    .filter(Analyst::isAvailable)
                    .min(byWorkload);
        }

        chosen.ifPresent(analyst -> {
            analyst.takeIncident();
            incident.assignTo(analyst);
        });
        return chosen;
    }
}
