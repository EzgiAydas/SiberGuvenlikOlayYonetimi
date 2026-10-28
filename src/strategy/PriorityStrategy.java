package strategy;

import model.SecurityIncident;

/**
 * Strategy kalıbı: öncelik skoru hesaplama yöntemi değiştirilebilir.
 * Skor 0-100 arasındadır; yüksek skor = daha acil.
 */
public interface PriorityStrategy {
    double calculate(SecurityIncident incident);
}
