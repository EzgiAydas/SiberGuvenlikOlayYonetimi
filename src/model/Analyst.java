package model;

import java.io.Serializable;

/** Olaylara atanabilen güvenlik analisti. */
public class Analyst implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int MAX_WORKLOAD = 5;

    private final String name;
    private final String expertise;
    private int workload;

    public Analyst(String name, String expertise) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Analist adı boş olamaz.");
        }
        this.name = name;
        this.expertise = expertise;
        this.workload = 0;
    }

    public boolean isAvailable() {
        return workload < MAX_WORKLOAD;
    }

    public void takeIncident() {
        if (!isAvailable()) {
            throw new IllegalStateException(name + " iş yükü dolu.");
        }
        workload++;
    }

    public void releaseIncident() {
        if (workload > 0) {
            workload--;
        }
    }

    public String getName() { return name; }
    public String getExpertise() { return expertise; }
    public int getWorkload() { return workload; }

    @Override
    public String toString() {
        return name + " (" + expertise + ", iş yükü: " + workload + "/" + MAX_WORKLOAD + ")";
    }
}
