package masjid.manager;

import masjid.interfaces.Manageable;
import masjid.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * MasjidManager - Central manager class handling all CRUD operations
 * and file I/O for the Masjid Management System.
 * Implements Manageable interface for Donors (demonstrates Interface usage).
 */
public class MasjidManager implements Manageable<Donor> {

    private ArrayList<Donor> donors;
    private ArrayList<DonationRecord> donations;
    private ArrayList<Announcement> announcements;
    private ArrayList<ImamStaff> staff;
    private java.util.LinkedHashMap<String, String> namazTimings;

    // File paths for data persistence
    private static final String DATA_DIR = "data";
    private static final String DONORS_FILE = DATA_DIR + "/donors.csv";
    private static final String DONATIONS_FILE = DATA_DIR + "/donations.csv";
    private static final String ANNOUNCEMENTS_FILE = DATA_DIR + "/announcements.csv";
    private static final String STAFF_FILE = DATA_DIR + "/staff.csv";
    private static final String NAMAZ_FILE = DATA_DIR + "/namaz.csv";

    // ID counters
    private int donorIdCounter = 1;
    private int donationIdCounter = 1;
    private int announcementIdCounter = 1;
    private int staffIdCounter = 1;

    public MasjidManager() {
        donors = new ArrayList<>();
        donations = new ArrayList<>();
        announcements = new ArrayList<>();
        staff = new ArrayList<>();
        namazTimings = new java.util.LinkedHashMap<>();

        // Create data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();

        // Load all data from files on startup
        loadAllData();
    }

    // ==================== DONOR CRUD (Manageable Interface) ====================

    @Override
    public void add(Donor donor) {
        donor.setId("D" + String.format("%03d", donorIdCounter++));
        donors.add(donor);
        saveDonors();
    }

    @Override
    public void update(String id, Donor updatedDonor) {
        for (int i = 0; i < donors.size(); i++) {
            if (donors.get(i).getId().equals(id)) {
                updatedDonor.setId(id);
                donors.set(i, updatedDonor);
                saveDonors();
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        donors.removeIf(d -> d.getId().equals(id));
        saveDonors();
    }

    @Override
    public Donor search(String id) {
        for (Donor d : donors) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    @Override
    public ArrayList<Donor> getAll() {
        return new ArrayList<>(donors);
    }

    @Override
    public ArrayList<Donor> searchByKeyword(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return donors.stream()
                .filter(d -> d.getName().toLowerCase().contains(lowerKey)
                        || d.getPhone().toLowerCase().contains(lowerKey)
                        || d.getAddress().toLowerCase().contains(lowerKey)
                        || d.getId().toLowerCase().contains(lowerKey)
                        || d.getDonorType().toLowerCase().contains(lowerKey))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==================== DONATION MANAGEMENT ====================

    public void addDonation(DonationRecord record) {
        record.setId("DN" + String.format("%03d", donationIdCounter++));
        donations.add(record);
        saveDonations();
    }

    public void deleteDonation(String id) {
        donations.removeIf(d -> d.getId().equals(id));
        saveDonations();
    }

    public ArrayList<DonationRecord> getAllDonations() {
        return new ArrayList<>(donations);
    }

    public ArrayList<DonationRecord> searchDonations(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return donations.stream()
                .filter(d -> d.getDonorName().toLowerCase().contains(lowerKey)
                        || d.getPurpose().toLowerCase().contains(lowerKey)
                        || d.getId().toLowerCase().contains(lowerKey)
                        || d.getDate().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public double getTotalDonations() {
        return donations.stream().mapToDouble(DonationRecord::getAmount).sum();
    }

    public double getTotalDonationsByPurpose(String purpose) {
        return donations.stream()
                .filter(d -> d.getPurpose().equalsIgnoreCase(purpose))
                .mapToDouble(DonationRecord::getAmount).sum();
    }

    // ==================== ANNOUNCEMENT MANAGEMENT ====================

    public void addAnnouncement(Announcement ann) {
        ann.setId("A" + String.format("%03d", announcementIdCounter++));
        announcements.add(ann);
        saveAnnouncements();
    }

    public void deleteAnnouncement(String id) {
        announcements.removeIf(a -> a.getId().equals(id));
        saveAnnouncements();
    }

    public ArrayList<Announcement> getAllAnnouncements() {
        return new ArrayList<>(announcements);
    }

    public ArrayList<Announcement> searchAnnouncements(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return announcements.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(lowerKey)
                        || a.getCategory().toLowerCase().contains(lowerKey)
                        || a.getDescription().toLowerCase().contains(lowerKey)
                        || a.getDate().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==================== STAFF MANAGEMENT ====================

    public void addStaff(ImamStaff s) {
        s.setId("S" + String.format("%03d", staffIdCounter++));
        staff.add(s);
        saveStaff();
    }

    public void updateStaff(String id, ImamStaff updated) {
        for (int i = 0; i < staff.size(); i++) {
            if (staff.get(i).getId().equals(id)) {
                updated.setId(id);
                staff.set(i, updated);
                saveStaff();
                return;
            }
        }
    }

    public void deleteStaff(String id) {
        staff.removeIf(s -> s.getId().equals(id));
        saveStaff();
    }

    public ArrayList<ImamStaff> getAllStaff() {
        return new ArrayList<>(staff);
    }

    public ArrayList<ImamStaff> searchStaff(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return staff.stream()
                .filter(s -> s.getName().toLowerCase().contains(lowerKey)
                        || s.getRole().toLowerCase().contains(lowerKey)
                        || s.getPhone().toLowerCase().contains(lowerKey)
                        || s.getId().toLowerCase().contains(lowerKey))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==================== DASHBOARD STATISTICS ====================

    public int getTotalDonorCount() { return donors.size(); }
    public int getTotalDonationCount() { return donations.size(); }
    public int getTotalAnnouncementCount() { return announcements.size(); }
    public int getTotalStaffCount() { return staff.size(); }

    // ==================== FILE I/O OPERATIONS ====================

    private void saveDonors() {
        saveToFile(DONORS_FILE, donors.stream().map(Donor::toCsv).collect(Collectors.toList()));
    }

    private void saveDonations() {
        saveToFile(DONATIONS_FILE, donations.stream().map(DonationRecord::toCsv).collect(Collectors.toList()));
    }

    private void saveAnnouncements() {
        saveToFile(ANNOUNCEMENTS_FILE, announcements.stream().map(Announcement::toCsv).collect(Collectors.toList()));
    }

    private void saveStaff() {
        saveToFile(STAFF_FILE, staff.stream().map(ImamStaff::toCsv).collect(Collectors.toList()));
    }

    private void saveToFile(String filePath, java.util.List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to " + filePath + ": " + e.getMessage());
        }
    }

    private void loadAllData() {
        loadDonors();
        loadDonations();
        loadAnnouncements();
        loadStaff();
        loadNamazTimings();
    }

    private void loadDonors() {
        ArrayList<String> lines = readFromFile(DONORS_FILE);
        for (String line : lines) {
            Donor d = Donor.fromCsv(line);
            if (d != null) {
                donors.add(d);
                updateCounter(d.getId(), "D");
            }
        }
    }

    private void loadDonations() {
        ArrayList<String> lines = readFromFile(DONATIONS_FILE);
        for (String line : lines) {
            DonationRecord r = DonationRecord.fromCsv(line);
            if (r != null) {
                donations.add(r);
                updateCounter(r.getId(), "DN");
            }
        }
    }

    private void loadAnnouncements() {
        ArrayList<String> lines = readFromFile(ANNOUNCEMENTS_FILE);
        for (String line : lines) {
            Announcement a = Announcement.fromCsv(line);
            if (a != null) {
                announcements.add(a);
                updateCounter(a.getId(), "A");
            }
        }
    }

    private void loadStaff() {
        ArrayList<String> lines = readFromFile(STAFF_FILE);
        for (String line : lines) {
            ImamStaff s = ImamStaff.fromCsv(line);
            if (s != null) {
                staff.add(s);
                updateCounter(s.getId(), "S");
            }
        }
    }

    private void updateCounter(String id, String prefix) {
        try {
            String numPart = id.substring(prefix.length());
            int num = Integer.parseInt(numPart);
            switch (prefix) {
                case "D":  if (num >= donorIdCounter) donorIdCounter = num + 1; break;
                case "DN": if (num >= donationIdCounter) donationIdCounter = num + 1; break;
                case "A":  if (num >= announcementIdCounter) announcementIdCounter = num + 1; break;
                case "S":  if (num >= staffIdCounter) staffIdCounter = num + 1; break;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Ignore invalid IDs
        }
    }

    private ArrayList<String> readFromFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from " + filePath + ": " + e.getMessage());
        }
        return lines;
    }

    /**
     * Save all data to files (called on application exit).
     */
    public void saveAllData() {
        saveDonors();
        saveDonations();
        saveAnnouncements();
        saveStaff();
        saveNamazTimings();
    }

    private void loadNamazTimings() {
        ArrayList<String> lines = readFromFile(NAMAZ_FILE);
        if (lines.isEmpty()) {
            namazTimings.put("Fajr", "04:45 AM");
            namazTimings.put("Dhuhr", "01:15 PM");
            namazTimings.put("Asr", "05:00 PM");
            namazTimings.put("Maghrib", "07:05 PM");
            namazTimings.put("Isha", "08:45 PM");
            namazTimings.put("Jummah", "01:30 PM");
            saveNamazTimings();
        } else {
            for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    namazTimings.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }

    public void saveNamazTimings() {
        ArrayList<String> lines = new ArrayList<>();
        for (java.util.Map.Entry<String, String> entry : namazTimings.entrySet()) {
            lines.add(entry.getKey() + "," + entry.getValue());
        }
        saveToFile(NAMAZ_FILE, lines);
    }

    public java.util.LinkedHashMap<String, String> getNamazTimings() {
        return new java.util.LinkedHashMap<>(namazTimings);
    }

    public void updateNamazTiming(String prayer, String time) {
        namazTimings.put(prayer, time);
        saveNamazTimings();
    }
}
