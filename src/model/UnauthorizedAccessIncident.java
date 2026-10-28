package model;

/** Yetkisiz erişim olayı. */
public class UnauthorizedAccessIncident extends SecurityIncident {

    private static final long serialVersionUID = 1L;

    private final String compromisedAccount;
    private final boolean privilegedAccount;

    public UnauthorizedAccessIncident(String id, String description, String affectedSystem,
                                      Severity severity, String compromisedAccount,
                                      boolean privilegedAccount) {
        super(id, IncidentType.UNAUTHORIZED_ACCESS, description, affectedSystem, severity);
        this.compromisedAccount = compromisedAccount;
        this.privilegedAccount = privilegedAccount;
    }

    @Override
    protected int typeRiskBonus() {
        return privilegedAccount ? 10 : 5;
    }

    @Override
    public String[] getResponsePlaybook() {
        return new String[] {
            "Ele geçirilen hesabı devre dışı bırak",
            "Aktif oturumları sonlandır ve şifreyi sıfırla",
            "Erişim loglarını incele",
            "Çok faktörlü doğrulamayı (MFA) zorunlu kıl"
        };
    }

    @Override
    public String getRequiredExpertise() {
        return "Kimlik ve Erişim Yönetimi";
    }

    public String getCompromisedAccount() { return compromisedAccount; }
    public boolean isPrivilegedAccount() { return privilegedAccount; }
}
