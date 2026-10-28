package strategy;

import model.SecurityIncident;

/**
 * Ağırlıklı skor: önem düzeyi ve türe özgü risk bonusunu 0-100'e ölçekleyip
 * ağırlıklarla birleştirir. Ağırlıklar dışarıdan verilebilir (Open/Closed).
 */
public class WeightedPriorityStrategy implements PriorityStrategy {

    private static final double MAX_SEVERITY_WEIGHT = 4.0;   // Severity.KRITIK
    private static final double MAX_RISK_BONUS = 12.0;       // alt sınıflardaki en yüksek bonus

    private final double severityWeight;
    private final double riskWeight;

    public WeightedPriorityStrategy() {
        this(0.7, 0.3);
    }

    public WeightedPriorityStrategy(double severityWeight, double riskWeight) {
        if (severityWeight < 0 || riskWeight < 0
                || Math.abs(severityWeight + riskWeight - 1.0) > 0.0001) {
            throw new IllegalArgumentException("Ağırlıklar negatif olamaz ve toplamı 1 olmalı.");
        }
        this.severityWeight = severityWeight;
        this.riskWeight = riskWeight;
    }

    @Override
    public double calculate(SecurityIncident incident) {
        int severityValue = incident.getSeverity().getWeight();
        // calculatePriorityScore() = önem * 10 + türe özgü bonus
        int riskBonus = incident.calculatePriorityScore() - severityValue * 10;

        double severityPart = severityValue / MAX_SEVERITY_WEIGHT * 100;
        double riskPart = Math.min(riskBonus, MAX_RISK_BONUS) / MAX_RISK_BONUS * 100;

        double score = severityPart * severityWeight + riskPart * riskWeight;
        return Math.round(score * 10) / 10.0;
    }
}
