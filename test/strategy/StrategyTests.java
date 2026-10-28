package strategy;

import model.*;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StrategyTests {

    private final PriorityStrategy strategy = new WeightedPriorityStrategy();

    private SecurityIncident ddos(String id, Severity severity) {
        return new DDoSIncident(id, "Yoğun trafik", "Web Sunucusu", severity, 150_000, "Asya");
    }

    private Analyst analyst(String name, String expertise) {
        return new Analyst(name, expertise);
    }

    // ---- Skor hesaplama ----

    @Test
    void kritikOlayDusukOlaydanYuksekSkorAlir() {
        double dusuk = strategy.calculate(ddos("A", Severity.DUSUK));
        double kritik = strategy.calculate(ddos("B", Severity.KRITIK));
        assertTrue(kritik > dusuk);
    }

    @Test
    void skorHerZamanSifirIleYuzArasindadir() {
        for (Severity s : Severity.values()) {
            double skor = strategy.calculate(ddos("X", s));
            assertTrue(skor >= 0 && skor <= 100, "Skor aralık dışı: " + skor);
        }
    }

    @Test
    void agirliklarinToplami1DegilseHataVerir() {
        assertThrows(IllegalArgumentException.class,
                () -> new WeightedPriorityStrategy(0.5, 0.8));
    }

    @Test
    void slaSuresiGecenOlayinSkoruArtar() {
        SecurityIncident olay = ddos("S1", Severity.KRITIK);          // SLA: 1 saat
        double normal = strategy.calculate(olay);

        Clock bes_saat_sonra = Clock.fixed(
                Instant.now().plus(Duration.ofHours(5)), ZoneId.systemDefault());
        SlaAwarePriorityStrategy sla = new SlaAwarePriorityStrategy(strategy, bes_saat_sonra);

        assertTrue(sla.calculate(olay) >= normal);
        assertTrue(sla.isSlaBreached(olay));
    }

    @Test
    void cozulmusOlayaSlaBonusuVerilmez() {
        SecurityIncident olay = ddos("S2", Severity.YUKSEK);
        for (int i = 0; i < 4; i++) {
            olay.advanceStatus();                                     // TESPIT -> COZULDU
        }
        Clock yirmi_saat_sonra = Clock.fixed(
                Instant.now().plus(Duration.ofHours(20)), ZoneId.systemDefault());
        SlaAwarePriorityStrategy sla = new SlaAwarePriorityStrategy(strategy, yirmi_saat_sonra);

        assertEquals(strategy.calculate(olay), sla.calculate(olay));
        assertFalse(sla.isSlaBreached(olay));
    }

    // ---- Öncelik kuyruğu (heap) ----

    @Test
    void kuyrukEnYuksekSkorluOlayiOnceVerir() {
        SecurityIncident dusuk = ddos("D", Severity.DUSUK);
        SecurityIncident kritik = ddos("K", Severity.KRITIK);

        IncidentPriorityQueue queue = new IncidentPriorityQueue(strategy);
        queue.add(dusuk);
        queue.add(kritik);

        assertSame(kritik, queue.poll());
        assertSame(dusuk, queue.poll());
        assertNull(queue.poll());
    }

    @Test
    void skorlarEsitseOnceEklenenOnceCikar() {
        SecurityIncident ilk = ddos("ILK", Severity.ORTA);
        SecurityIncident ikinci = ddos("IKINCI", Severity.ORTA);

        IncidentPriorityQueue queue = new IncidentPriorityQueue(strategy);
        queue.add(ilk);
        queue.add(ikinci);

        assertSame(ilk, queue.poll());
    }

    @Test
    void toSortedListKuyrugunIceriginiBozmaz() {
        IncidentPriorityQueue queue = new IncidentPriorityQueue(strategy);
        queue.add(ddos("A", Severity.ORTA));
        queue.add(ddos("B", Severity.YUKSEK));

        List<SecurityIncident> sirali = queue.toSortedList();

        assertEquals("B", sirali.get(0).getId());
        assertEquals(2, queue.size());
    }

    @Test
    void olayOnemiDegisinceRebuildSiralamayiGunceller() {
        SecurityIncident a = ddos("A", Severity.ORTA);
        SecurityIncident b = ddos("B", Severity.YUKSEK);

        IncidentPriorityQueue queue = new IncidentPriorityQueue(strategy);
        queue.add(a);
        queue.add(b);

        a.escalate();                    // ORTA -> YUKSEK
        a.escalate();                    // YUKSEK -> KRITIK
        queue.rebuild();

        assertSame(a, queue.poll());
    }

    @Test
    void removeKuyrukTanOlayiCikarir() {
        SecurityIncident a = ddos("A", Severity.ORTA);
        IncidentPriorityQueue queue = new IncidentPriorityQueue(strategy);
        queue.add(a);

        assertTrue(queue.remove(a));
        assertTrue(queue.isEmpty());
    }

    // ---- Analist atama ----

    @Test
    void atamaUzmanligiUyanEnAzYukluAnalistiSecer() {
        Analyst yorgun = analyst("Ali", "Ağ Güvenliği");
        Analyst bos = analyst("Ayşe", "Ağ Güvenliği");
        yorgun.takeIncident();
        yorgun.takeIncident();

        SecurityIncident olay = ddos("D1", Severity.KRITIK);
        Optional<Analyst> secilen = new WorkloadBasedAssigner().assign(olay, List.of(yorgun, bos));

        assertTrue(secilen.isPresent());
        assertEquals("Ayşe", secilen.get().getName());
        assertEquals(1, bos.getWorkload());
        assertSame(bos, olay.getAssignedAnalyst());
    }

    @Test
    void uzmanYoksaMusaitEnAzYukluAnalistiSecer() {
        Analyst epostaUzmani = analyst("Can", "E-posta Güvenliği");

        SecurityIncident olay = ddos("D2", Severity.YUKSEK);     // Ağ Güvenliği ister
        Optional<Analyst> secilen = new WorkloadBasedAssigner().assign(olay, List.of(epostaUzmani));

        assertTrue(secilen.isPresent());
        assertEquals("Can", secilen.get().getName());
    }

    @Test
    void isYukuDolanAnalistSecilmez() {
        Analyst dolu = analyst("Dolu", "Ağ Güvenliği");
        for (int i = 0; i < Analyst.MAX_WORKLOAD; i++) {
            dolu.takeIncident();
        }
        Optional<Analyst> sonuc = new WorkloadBasedAssigner()
                .assign(ddos("D3", Severity.ORTA), List.of(dolu));

        assertTrue(sonuc.isEmpty());
    }

    @Test
    void analistListesiBossaOptionalEmptyDoner() {
        Optional<Analyst> sonuc = new WorkloadBasedAssigner()
                .assign(ddos("D4", Severity.ORTA), List.of());
        assertTrue(sonuc.isEmpty());
    }
}
