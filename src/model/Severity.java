package model;

/** Olay önem düzeyi. Ağırlık, öncelik skoru hesabında kullanılır. */
public enum Severity {
    DUSUK("DÜŞÜK", 1),
    ORTA("ORTA", 2),
    YUKSEK("YÜKSEK", 3),
    KRITIK("KRİTİK", 4);

    private final String label;
    private final int weight;

    Severity(String label, int weight) {
        this.label = label;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return label;
    }
}
