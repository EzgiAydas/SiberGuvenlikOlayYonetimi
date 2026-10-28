package model;

/** Hizmet engelleme (DDoS) saldırısı. */
public class DDoSIncident extends SecurityIncident {

    private static final long serialVersionUID = 1L;

    private final int requestsPerSecond;
    private final String sourceRegion;

    public DDoSIncident(String id, String description, String affectedSystem,
                        Severity severity, int requestsPerSecond, String sourceRegion) {
        super(id, IncidentType.DDOS, description, affectedSystem, severity);
        this.requestsPerSecond = requestsPerSecond;
        this.sourceRegion = sourceRegion;
    }

    @Override
    protected int typeRiskBonus() {
        if (requestsPerSecond > 100_000) return 10;
        if (requestsPerSecond > 10_000) return 6;
        return 3;
    }

    @Override
    public String[] getResponsePlaybook() {
        return new String[] {
            "Trafik filtreleme / rate limiting uygula",
            "CDN veya DDoS koruma servisini etkinleştir",
            "Saldırı kaynağı IP aralıklarını engelle",
            "Sunucu kapasitesini izle ve gerekirse ölçekle"
        };
    }

    @Override
    public String getRequiredExpertise() {
        return "Ağ Güvenliği";
    }

    public int getRequestsPerSecond() { return requestsPerSecond; }
    public String getSourceRegion() { return sourceRegion; }
}
