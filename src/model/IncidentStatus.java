package model;

/** Olayın yaşam döngüsü aşamaları (sırayla ilerler). */
public enum IncidentStatus {
    TESPIT("TESPİT"),
    TRIYAJ("TRİYAJ"),
    MUDAHALE("MÜDAHALE"),
    SINIRLANDIRMA("SINIRLANDIRMA"),
    COZULDU("ÇÖZÜLDÜ"),
    KAPATILDI("KAPATILDI");

    private final String label;

    IncidentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Bir sonraki aşama; son aşamadaysa kendisini döner. */
    public IncidentStatus next() {
        IncidentStatus[] all = values();
        return ordinal() < all.length - 1 ? all[ordinal() + 1] : this;
    }

    public boolean isClosed() {
        return this == KAPATILDI;
    }

    @Override
    public String toString() {
        return label;
    }
}
