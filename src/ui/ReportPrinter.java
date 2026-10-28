package ui;

import model.SecurityIncident;
import java.util.List;

public class ReportPrinter {

    public void printHeader(String title) {
        System.out.println("\n==========================================");
        System.out.println("   " + title);
        System.out.println("==========================================");
    }

    public void printIncident(SecurityIncident incident) {
        System.out.println("------------------------------------------");
        System.out.println("Olay ID        : " + incident.getId());
        System.out.println("Olay Türü      : " + incident.getType());
        System.out.println("Açıklama       : " + incident.getDescription());
        System.out.println("Şiddet Seviyesi: " + incident.getSeverity());
        System.out.println("Durum          : " + incident.getStatus());
        System.out.println("------------------------------------------");
    }

    public void printIncidentList(List<SecurityIncident> incidents) {
        printHeader("SİBER GÜVENLİK OLAY LİSTESİ (" + incidents.size() + " Adet)");
        if (incidents.isEmpty()) {
            System.out.println("Kayıtlı herhangi bir güvenlik olayı bulunamadı.");
            return;
        }
        for (SecurityIncident incident : incidents) {
            printIncident(incident);
        }
    }
}