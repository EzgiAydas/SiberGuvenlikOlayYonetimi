package factory;

import model.*;


/**
 * Factory Method kalıbı: olay türüne göre doğru alt sınıfı üretir.
 * İstemci kodu somut sınıfları bilmek zorunda kalmaz (Open/Closed, DIP).
 *
 * Türe özgü ek bilgiler, sırayla "extras" parametresiyle verilir:
 *  PHISHING            -> gönderen adresi, hedeflenen kullanıcı sayısı
 *  DDOS                -> saniyedeki istek sayısı, kaynak bölge
 *  MALWARE             -> zararlı adı, ağ üzerinden yayılıyor mu
 *  UNAUTHORIZED_ACCESS -> ele geçirilen hesap, yetkili hesap mı
 *  DATA_BREACH         -> sızan kayıt sayısı, kişisel veri var mı
 */
public class IncidentFactory {

    private int counter = 0;

    public SecurityIncident create(IncidentType type, String description,
                                   String affectedSystem, Severity severity,
                                   Object... extras) {
        if (type == null) {
            throw new IllegalArgumentException("Olay türü boş olamaz.");
        }
        String id = nextId(type);
        try {
            return switch (type) {
                case PHISHING -> new PhishingIncident(id, description, affectedSystem, severity,
                        (String) extras[0], (Integer) extras[1]);
                case DDOS -> new DDoSIncident(id, description, affectedSystem, severity,
                        (Integer) extras[0], (String) extras[1]);
                case MALWARE -> new MalwareIncident(id, description, affectedSystem, severity,
                        (String) extras[0], (Boolean) extras[1]);
                case UNAUTHORIZED_ACCESS -> new UnauthorizedAccessIncident(id, description,
                        affectedSystem, severity, (String) extras[0], (Boolean) extras[1]);
                case DATA_BREACH -> new DataBreachIncident(id, description, affectedSystem,
                        severity, (Integer) extras[0], (Boolean) extras[1]);
            };
        } catch (ArrayIndexOutOfBoundsException | ClassCastException e) {
            throw new IllegalArgumentException(
                    type + " için ek parametreler eksik veya hatalı.", e);
        }
    }

    private String nextId(IncidentType type) {
        counter++;
        return String.format("INC-%s-%03d", type.name().substring(0, 3), counter);
    }
}