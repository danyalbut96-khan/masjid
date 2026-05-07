package masjid.model;

/**
 * Donor class - extends Person (Inheritance).
 * Represents a donor who contributes to the Masjid.
 */
public class Donor extends Person {
    private String donorType; // Regular, One-time, Monthly

    public Donor() {
        super();
        this.donorType = "One-time";
    }

    public Donor(String id, String name, String phone, String address, String donorType) {
        super(id, name, phone, address);
        this.donorType = donorType;
    }

    // Polymorphism - implementing abstract method
    @Override
    public String getRole() {
        return "Donor";
    }

    public String getDonorType() { return donorType; }
    public void setDonorType(String donorType) { this.donorType = donorType; }

    @Override
    public String toCsv() {
        return super.toCsv() + "," + donorType;
    }

    /**
     * Parse a Donor from a CSV line.
     */
    public static Donor fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 5) {
            return new Donor(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[4].trim());
        }
        return null;
    }

    @Override
    public String toString() {
        return getName() + " (" + donorType + ")";
    }
}
