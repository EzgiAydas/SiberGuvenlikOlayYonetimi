package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tüm güvenlik olaylarının ortak üst sınıfı.
 * Ortak alanlar ve metotlar burada, türe özgü davranış alt sınıflarda (polimorfizm).
 */
public abstract class SecurityIncident implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final String id;
    private final IncidentType type;
    private final String description;
    private final String affectedSystem;
    private final LocalDateTime detectedAt;
    private Severity severity;
    private IncidentStatus status;
    private Analyst assignedAnalyst;

    protected SecurityIncident(String id, IncidentType type, String description,
                               String affectedSystem, Severity severity) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Olay ID boş olamaz.");
        }
        if (type == null || severity == null) {
            throw new IllegalArgumentException("Tür ve önem düzeyi boş olamaz.");
        }
        this.id = id;
        this.type = type;
        this.description = description;
        this.affectedSystem = affectedSystem;
        this.severity = severity;
        this.status = IncidentStatus.TESPIT;
        this.detectedAt = LocalDateTime.now();
    }

    // ---- Türe özgü davranışlar (alt sınıflar doldurur) ----

    /** Türe özgü ek puan; öncelik skoruna eklenir. */
    protected abstract int typeRiskBonus();

    /** Bu olay türü için önerilen müdahale adımları. */
    public abstract String[] getResponsePlaybook();

    /** Müdahale için gereken uzmanlık alanı (analist eşleştirmede kullanılır). */
    public abstract String getRequiredExpertise();

    // ---- Ortak davranışlar ----

    /** Öncelik skoru = önem ağırlığı * 10 + türe özgü bonus. */
    public final int calculatePriorityScore() {
        return severity.getWeight() * 10 + typeRiskBonus();
    }

    /** Olayı bir sonraki aşamaya taşır. */
    public void advanceStatus() {
        if (status.isClosed()) {
            throw new IllegalStateException("Olay zaten kapatıldı: " + id);
        }
        status = status.next();
    }

    public void escalate() {
        Severity[] all = Severity.values();
        if (severity.ordinal() < all.length - 1) {
            severity = all[severity.ordinal() + 1];
        }
    }

    public void assignTo(Analyst analyst) {
        this.assignedAnalyst = analyst;
    }

    // ---- Getter'lar ----

    public String getId() { return id; }
    public IncidentType getType() { return type; }
    public String getDescription() { return description; }
    public String getAffectedSystem() { return affectedSystem; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public Severity getSeverity() { return severity; }
    public IncidentStatus getStatus() { return status; }
    public Analyst getAssignedAnalyst() { return assignedAnalyst; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Skor: %d | %s | Analist: %s",
                id, type.getDisplayName(), severity, status, calculatePriorityScore(),
                detectedAt.format(FMT),
                assignedAnalyst == null ? "atanmadı" : assignedAnalyst.getName());
    }
}
