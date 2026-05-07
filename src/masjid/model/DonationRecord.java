package masjid.model;

/**
 * DonationRecord class to track individual donations.
 * Links to a Donor by donorId.
 */
public class DonationRecord {
    private String id;
    private String donorId;
    private String donorName;
    private double amount;
    private String date;
    private String purpose; // Zakat, Sadaqah, Building Fund, General, Ramadan

    public DonationRecord() {
        this.id = "";
        this.donorId = "";
        this.donorName = "";
        this.amount = 0.0;
        this.date = "";
        this.purpose = "General";
    }

    public DonationRecord(String id, String donorId, String donorName,
                          double amount, String date, String purpose) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.amount = amount;
        this.date = date;
        this.purpose = purpose;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String toCsv() {
        return id + "," + donorId + "," + donorName + "," + amount + "," + date + "," + purpose;
    }

    public static DonationRecord fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 6) {
            try {
                double amt = Double.parseDouble(parts[3].trim());
                return new DonationRecord(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                        amt, parts[4].trim(), parts[5].trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return donorName + " donated $" + String.format("%.2f", amount) + " for " + purpose;
    }
}
