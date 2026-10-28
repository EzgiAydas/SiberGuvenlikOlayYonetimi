package ui;

import factory.IncidentFactory;
import model.Analyst;
import model.IncidentType;
import model.SecurityIncident;
import model.Severity;
import observer.CriticalAlertObserver;
import repository.FileIncidentRepository;
import service.IncidentManager;
import strategy.AnalystAssigner;
import strategy.IncidentPriorityQueue;
import strategy.WeightedPriorityStrategy;
import strategy.WorkloadBasedAssigner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsoleMenu {
    private final IncidentManager incidentManager;
    private final InputReader inputReader;
    private final ReportPrinter reportPrinter;
    private final IncidentPriorityQueue priorityQueue;
    private final AnalystAssigner analystAssigner;
    private final IncidentFactory incidentFactory = new IncidentFactory();
    private final List<Analyst> analysts = new ArrayList<>();

    public ConsoleMenu() {
        FileIncidentRepository repo = new FileIncidentRepository();
        this.incidentManager = new IncidentManager(repo);
        this.incidentManager.addObserver(new CriticalAlertObserver());
        this.inputReader = new InputReader();
        this.reportPrinter = new ReportPrinter();
        this.priorityQueue = new IncidentPriorityQueue(new WeightedPriorityStrategy());
        this.analystAssigner = new WorkloadBasedAssigner();
    }

    public void start() {
        boolean running = true;
        while (running) {
            reportPrinter.printHeader("SİBER GÜVENLİK OLAY YÖNETİM SİSTEMİ");
            System.out.println("1. Yeni Güvenlik Olayı Bildir");
            System.out.println("2. Tüm Olayları Listele");
            System.out.println("3. Olay Öncelik Skoru Hesapla & Sırala");
            System.out.println("4. Olayı Analiste Ata");
            System.out.println("5. Çıkış");
            System.out.println("------------------------------------------");

            int choice = inputReader.readInt("Seçiminiz", 1, 5);
            switch (choice) {
                case 1:
                    createIncidentMenu();
                    break;
                case 2:
                    reportPrinter.printIncidentList(incidentManager.getAllIncidents());
                    break;
                case 3:
                    calculatePriorities();
                    break;
                case 4:
                    assignAnalyst();
                    break;
                case 5:
                    System.out.println("Sistemden çıkılıyor. Güvenli günler dileriz!");
                    running = false;
                    break;
            }
        }
    }

    private void createIncidentMenu() {
        reportPrinter.printHeader("YENİ OLAY BİLDİRİMİ");
        System.out.println("1. Phishing (Oltalama)");
        System.out.println("2. DDoS Saldırısı");
        System.out.println("3. Malware (Zararlı Yazılım)");
        System.out.println("4. Veri İhlali (Data Breach)");
        System.out.println("5. Yetkisiz Erişim (Unauthorized Access)");

        int typeChoice = inputReader.readInt("Olay Türü Seçin", 1, 5);
        // enum sırasına güvenmemek için açık eşleme
        IncidentType[] types = {
                IncidentType.PHISHING, IncidentType.DDOS, IncidentType.MALWARE,
                IncidentType.DATA_BREACH, IncidentType.UNAUTHORIZED_ACCESS
        };
        IncidentType type = types[typeChoice - 1];

        String desc = inputReader.readString("Olay Açıklaması");
        String system = inputReader.readString("Etkilenen Sistem");

        System.out.println("\nŞiddet Seviyesi Seçin:");
        System.out.println("1. DUSUK");
        System.out.println("2. ORTA");
        System.out.println("3. YUKSEK");
        System.out.println("4. KRITIK");
        int sevChoice = inputReader.readInt("Seçim", 1, 4);
        Severity severity = Severity.values()[sevChoice - 1];

        Object[] extras = readExtras(type);

        SecurityIncident incident =
                incidentFactory.create(type, desc, system, severity, extras);
        incidentManager.createIncident(incident);
        priorityQueue.add(incident);

        System.out.println("\n[BAŞARILI] " + incident.getId()
                + " numaralı olay sisteme kaydedildi!");
    }

    /** Türe özgü ek bilgileri sırasıyla IncidentFactory'nin beklediği tiplerde döndürür. */
    private Object[] readExtras(IncidentType type) {
        switch (type) {
            case PHISHING:
                return new Object[]{
                        inputReader.readString("Gönderen adresi"),
                        inputReader.readInt("Hedeflenen kullanıcı sayısı", 0, Integer.MAX_VALUE)};
            case DDOS:
                return new Object[]{
                        inputReader.readInt("Saniyedeki istek sayısı", 0, Integer.MAX_VALUE),
                        inputReader.readString("Kaynak bölge")};
            case MALWARE:
                return new Object[]{
                        inputReader.readString("Zararlı adı"),
                        readYesNo("Ağ üzerinden yayılıyor mu?")};
            case UNAUTHORIZED_ACCESS:
                return new Object[]{
                        inputReader.readString("Ele geçirilen hesap"),
                        readYesNo("Yetkili (admin) hesap mı?")};
            case DATA_BREACH:
                return new Object[]{
                        inputReader.readInt("Sızan kayıt sayısı", 0, Integer.MAX_VALUE),
                        readYesNo("Kişisel veri var mı?")};
            default:
                return new Object[0];
        }
    }

    private boolean readYesNo(String question) {
        return inputReader.readString(question + " (E/H)").trim().equalsIgnoreCase("E");
    }

    private void calculatePriorities() {
        reportPrinter.printHeader("ÖNCELİKLENDİRİLMİŞ OLAY KUYRUĞU");
        if (priorityQueue.isEmpty()) {
            System.out.println("İşlenecek olay bulunmamaktadır.");
            return;
        }
        priorityQueue.rebuild(); // SLA bonusu zamanla değiştiği için skorları yenile
        // Kuyruğu boşaltmadan sıralı liste (poll olsaydı olaylar kuyruktan silinirdi)
        for (SecurityIncident inc : priorityQueue.toSortedList()) {
            reportPrinter.printIncident(inc);
        }
    }

    private void assignAnalyst() {
        reportPrinter.printHeader("ANALİST ATAMA İŞLEMİ");

        if (incidentManager.getAllIncidents().isEmpty()) {
            System.out.println("Atanacak olay bulunmamaktadır.");
            return;
        }

        String analystName = inputReader.readString("Analist Adı");
        String expertise = inputReader.readString("Uzmanlık Alanı");
        analysts.add(new Analyst(analystName, expertise));

        SecurityIncident target = incidentManager.getAllIncidents().get(0);
        Optional<Analyst> assigned = analystAssigner.assign(target, analysts);

        if (assigned.isPresent()) {
            System.out.println("[BAŞARILI] " + target.getId() + " id'li olay "
                    + assigned.get().getName() + " isimli analiste atandı.");
        } else {
            System.out.println("Uygun analist bulunamadı.");
        }
    }
}