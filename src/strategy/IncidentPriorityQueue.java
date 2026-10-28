package strategy;

import model.SecurityIncident;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Heap (PriorityQueue) tabanlı öncelik kuyruğu.
 * En yüksek skorlu olay en başta çıkar; skorlar eşitse önce tespit edilen önce çıkar.
 * Ekleme/çıkarma O(log n), en öncelikli olaya erişim O(1).
 */
public class IncidentPriorityQueue {

    /** Bir olay ve eklendiği andaki skoru. */
    public record ScoredIncident(SecurityIncident incident, double score) { }

    private static final class Entry {
        final SecurityIncident incident;
        final double score;
        final long sequence;

        Entry(SecurityIncident incident, double score, long sequence) {
            this.incident = incident;
            this.score = score;
            this.sequence = sequence;
        }
    }

    private static final Comparator<Entry> ORDER =
            Comparator.comparingDouble((Entry e) -> e.score).reversed()
                      .thenComparing(e -> e.incident.getDetectedAt())
                      .thenComparingLong(e -> e.sequence);

    private final PriorityStrategy strategy;
    private final PriorityQueue<Entry> heap = new PriorityQueue<>(ORDER);
    private long sequence = 0;

    public IncidentPriorityQueue(PriorityStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strateji boş olamaz.");
        }
        this.strategy = strategy;
    }

    public void add(SecurityIncident incident) {
        heap.add(new Entry(incident, strategy.calculate(incident), sequence++));
    }

    /** En öncelikli olayı kuyruktan çıkarır; kuyruk boşsa null döner. */
    public SecurityIncident poll() {
        Entry e = heap.poll();
        return e == null ? null : e.incident;
    }

    public SecurityIncident peek() {
        Entry e = heap.peek();
        return e == null ? null : e.incident;
    }

    /** Çözülen/kapatılan olayı kuyruktan çıkarmak için. */
    public boolean remove(SecurityIncident incident) {
        return heap.removeIf(e -> e.incident == incident);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    /** Kuyruğu bozmadan, öncelik sırasına göre skorlarıyla birlikte liste verir. */
    public List<ScoredIncident> toSortedScoredList() {
        PriorityQueue<Entry> copy = new PriorityQueue<>(heap);
        List<ScoredIncident> result = new ArrayList<>();
        while (!copy.isEmpty()) {
            Entry e = copy.poll();
            result.add(new ScoredIncident(e.incident, e.score));
        }
        return result;
    }

    public List<SecurityIncident> toSortedList() {
        List<SecurityIncident> result = new ArrayList<>();
        for (ScoredIncident s : toSortedScoredList()) {
            result.add(s.incident());
        }
        return result;
    }

    /**
     * Skorları yeniden hesaplayıp heap'i baştan kurar.
     * Olayın önemi değişince (escalate) veya SLA bonusu zamanla artınca gerekir,
     * çünkü PriorityQueue içindeki elemanların sırasını kendiliğinden güncellemez.
     */
    public void rebuild() {
        List<Entry> old = new ArrayList<>(heap);
        heap.clear();
        for (Entry e : old) {
            heap.add(new Entry(e.incident, strategy.calculate(e.incident), e.sequence));
        }
    }
}
