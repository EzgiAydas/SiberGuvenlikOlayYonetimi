package repository;

import model.SecurityIncident;
import java.io.*;
import java.util.*;

public class FileIncidentRepository implements IncidentRepository {
    private final String filePath = "incidents.ser";
    private Map<String, SecurityIncident> storage = new HashMap<>();

    public FileIncidentRepository() {
        loadFromFile();
    }

    @Override
    public void save(SecurityIncident incident) {
        storage.put(incident.getId(), incident);
        saveToFile();
    }

    @Override
    public Optional<SecurityIncident> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<SecurityIncident> findAll() {
        return new ArrayList<>(storage.values());
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            storage = (Map<String, SecurityIncident>) ois.readObject();
        } catch (Exception e) {
            storage = new HashMap<>();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
