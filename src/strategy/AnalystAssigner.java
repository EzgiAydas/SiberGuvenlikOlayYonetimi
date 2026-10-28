package strategy;

import model.Analyst;
import model.SecurityIncident;

import java.util.List;
import java.util.Optional;

/** Analist atama sözleşmesi (Dependency Inversion: Manager bu arayüze bağımlı). */
public interface AnalystAssigner {
    /**
     * Olay için uygun analisti seçer, olaya atar ve analistin iş yükünü artırır.
     * Uygun analist yoksa Optional.empty() döner.
     */
    Optional<Analyst> assign(SecurityIncident incident, List<Analyst> analysts);
}
