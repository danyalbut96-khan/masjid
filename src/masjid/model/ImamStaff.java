package masjid.model;

/**
 * ImamStaff class - extends Person (Inheritance).
 * Represents Imam, Muezzin, Teacher, Cleaner, or other staff.
 */
public class ImamStaff extends Person {
    private String role;      // Imam, Muezzin, Teacher, Cleaner, Admin
    private String salary;
    private String joinDate;

    public ImamStaff() {
        super();
        this.role = "Staff";
        this.salary = "0";
        this.joinDate = "";
    }

    public ImamStaff(String id, String name, String phone, String address,
                     String role, String salary, String joinDate) {
        super(id, name, phone, address);
        this.role = role;
        this.salary = salary;
        this.joinDate = joinDate;
    }

    // Polymorphism - implementing abstract method
    @Override
    public String getRole() {
        return role;
    }

    public void setRole(String role) { this.role = role; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }

    @Override
    public String toCsv() {
        return super.toCsv() + "," + role + "," + salary + "," + joinDate;
    }

    /**
     * Parse an ImamStaff from a CSV line.
     */
    public static ImamStaff fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 7) {
            return new ImamStaff(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[4].trim(), parts[5].trim(), parts[6].trim());
        }
        return null;
    }

    @Override
    public String toString() {
        return getName() + " (" + role + ")";
    }
}
