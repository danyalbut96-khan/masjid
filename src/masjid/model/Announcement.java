package masjid.model;

/**
 * Announcement class for Masjid announcements.
 * Categories: Juma, Event, Ramadan, General
 */
public class Announcement {
    private String id;
    private String title;
    private String description;
    private String category; // Juma, Event, Ramadan, General
    private String date;

    public Announcement() {
        this.id = "";
        this.title = "";
        this.description = "";
        this.category = "General";
        this.date = "";
    }

    public Announcement(String id, String title, String description, String category, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String toCsv() {
        // Replace commas in description with semicolons for CSV safety
        String safeDesc = description.replace(",", ";");
        return id + "," + title + "," + safeDesc + "," + category + "," + date;
    }

    public static Announcement fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 5) {
            String desc = parts[2].trim().replace(";", ",");
            return new Announcement(parts[0].trim(), parts[1].trim(), desc,
                    parts[3].trim(), parts[4].trim());
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + category + "] " + title + " - " + date;
    }
}
