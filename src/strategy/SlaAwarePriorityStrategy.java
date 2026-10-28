package strategy;

import model.IncidentStatus;
import model.SecurityIncident;
import model.Severity;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Decorator mantığı: mevcut bir stratejiyi sarar ve olay beklemeye devam ettikçe
 * skora bonus ekler. SLA süresi önem düzeyine göre değişir.
 * Çözülmüş veya kapatılmış olaylara bonus verilmez.
 */
public class SlaAwarePriorityStrategy implements PriorityStrategy {

    private static final double MAX_BONUS = 20.0;
    private static final double BONUS_AT_SLA_LIMIT = 10.0;   // SLA süresi dolunca eklenen bonus

    private final PriorityStrategy baseStrategy;
    private final Clock clock;

    public SlaAwarePriorityStrategy(PriorityStrategy baseStrategy) {
        this(baseStrategy, Clock.systemDefaultZone());
    }

    // Clock dışarıdan verilebildiği için testte zaman "ileri sarılabilir"
    public SlaAwarePriorityStrategy(PriorityStrategy baseStrategy, Clock clock) {
        this.baseStrategy = baseStrategy;
        this.clock = clock;
    }

    /** Önem düzeyine göre müdahale süresi (saat). */
    public static long slaHours(Severity severity) {
        return switch (severity) {
            case KRITIK -> 1;
            case YUKSEK -> 4;
            case ORTA -> 24;
            case DUSUK -> 72;
        };
    }

    public boolean isSlaBreached(SecurityIncident incident) {
        return isActive(incident) && waitingHours(incident) > slaHours(incident.getSeverity());
    }

    @Override
    public double calculate(SecurityIncident incident) {
        double base = baseStrategy.calculate(incident);
        if (!isActive(incident)) {
            return base;
        }
        double ratio = waitingHours(incident) / slaHours(incident.getSeverity());
        double bonus = Math.min(ratio * BONUS_AT_SLA_LIMIT, MAX_BONUS);
        return Math.min(100.0, Math.round((base + bonus) * 10) / 10.0);
    }

    private boolean isActive(SecurityIncident incident) {
        return incident.getStatus().ordinal() < IncidentStatus.COZULDU.ordinal();
    }

    private double waitingHours(SecurityIncident incident) {
        Duration waited = Duration.between(incident.getDetectedAt(), LocalDateTime.now(clock));
        return Math.max(0, waited.toSeconds() / 3600.0);
    }
}
