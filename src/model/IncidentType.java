package model;

/** Olay türü. Factory ve kayıt/raporlama için kullanılır. */
public enum IncidentType {
    PHISHING("Oltalama"),
    DDOS("Hizmet Engelleme Saldırısı"),
    MALWARE("Zararlı Yazılım"),
    UNAUTHORIZED_ACCESS("Yetkisiz Erişim"),
    DATA_BREACH("Veri Sızıntısı");

    private final String displayName;

    IncidentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
