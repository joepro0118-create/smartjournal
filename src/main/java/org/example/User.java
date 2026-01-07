package org.example; // Ensure this matches your project's package!

public class User {
    private String email;
    private String displayName;
    private String password;

    public User(String email, String displayName, String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    // Getters
    public String getEmail() { return email; }
    public  String getDisplayName() { return displayName; }
    public String getPassword() { return password; }

    // Helper to format data for the text file
    @Override
    public String toString() {
        return email + "\n" + displayName + "\n" + password;
    }
}