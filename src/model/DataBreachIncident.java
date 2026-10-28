package model;

/** Veri sızıntısı olayı. */
public class DataBreachIncident extends SecurityIncident {

    private static final long serialVersionUID = 1L;

    private final int leakedRecordCount;
    private final boolean containsPersonalData;

    public DataBreachIncident(String id, String description, String affectedSystem,
                              Severity severity, int leakedRecordCount,
                              boolean containsPersonalData) {
        super(id, IncidentType.DATA_BREACH, description, affectedSystem, severity);
        this.leakedRecordCount = leakedRecordCount;
        this.containsPersonalData = containsPersonalData;
    }

    @Override
    protected int typeRiskBonus() {
        int bonus = leakedRecordCount > 10_000 ? 8 : 4;
        return containsPersonalData ? bonus + 4 : bonus;
    }

    @Override
    public String[] getResponsePlaybook() {
        return new String[] {
            "Sızıntı kaynağını kapat ve erişimi kısıtla",
            "Etkilenen veri kapsamını belirle",
            "Kişisel veri varsa KVKK kapsamında bildirim sürecini başlat",
            "Etkilenen kişileri bilgilendir"
        };
    }

    @Override
    public String getRequiredExpertise() {
        return "Veri Koruma ve Adli Bilişim";
    }

    public int getLeakedRecordCount() { return leakedRecordCount; }
    public boolean isContainsPersonalData() { return containsPersonalData; }
}
