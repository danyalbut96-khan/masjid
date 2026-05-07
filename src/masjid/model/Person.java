package masjid.model;

/**
 * Abstract class representing a person in the Masjid system.
 * Demonstrates Abstraction and Encapsulation OOP concepts.
 */
public abstract class Person {
    private String id;
    private String name;
    private String phone;
    private String address;

    // Default constructor
    public Person() {
        this.id = "";
        this.name = "";
        this.phone = "";
        this.address = "";
    }

    // Parameterized constructor
    public Person(String id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Abstract method - demonstrates Abstraction
    public abstract String getRole();

    // Getters and Setters - demonstrates Encapsulation
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Polymorphism - toString override
    @Override
    public String toString() {
        return getRole() + " | " + name + " | " + phone;
    }

    // Convert to CSV format for file storage
    public String toCsv() {
        return id + "," + name + "," + phone + "," + address;
    }
}
