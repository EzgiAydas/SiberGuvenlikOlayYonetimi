package model;

/** Oltalama (phishing) olayı. */
public class PhishingIncident extends SecurityIncident {

    private static final long serialVersionUID = 1L;

    private final String senderAddress;
    private final int targetedUserCount;

    public PhishingIncident(String id, String description, String affectedSystem,
                            Severity severity, String senderAddress, int targetedUserCount) {
        super(id, IncidentType.PHISHING, description, affectedSystem, severity);
        this.senderAddress = senderAddress;
        this.targetedUserCount = targetedUserCount;
    }

    @Override
    protected int typeRiskBonus() {
        return targetedUserCount > 50 ? 8 : 4;
    }

    @Override
    public String[] getResponsePlaybook() {
        return new String[] {
            "Gönderen adresini ve alan adını engelle",
            "E-postayı tüm posta kutularından geri çek",
            "Linke tıklayan kullanıcıların şifrelerini sıfırla",
            "Kullanıcıları bilgilendir ve farkındalık uyarısı gönder"
        };
    }

    @Override
    public String getRequiredExpertise() {
        return "E-posta Güvenliği";
    }

    public String getSenderAddress() { return senderAddress; }
    public int getTargetedUserCount() { return targetedUserCount; }
}
