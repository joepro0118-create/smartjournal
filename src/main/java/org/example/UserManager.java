package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UserManager {
    private static final String FILE_PATH = "UserData.txt";

    public UserManager() {
        // 1. Initialize Database Table
        DatabaseHelper.createTable();

        // 2. Perform File I/O: Load existing text file data into the database
        // This ensures you keep your "Basic Feature" mark for File I/O
        loadUsersAndSyncToDB();
    }

    private void loadUsersAndSyncToDB() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        // Create a list to track emails found in the text file
        java.util.List<String> validEmails = new java.util.ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String email;
            while ((email = reader.readLine()) != null) {
                email = email.trim();
                if (email.isEmpty()) continue;

                String name = reader.readLine();
                String hashedPass = reader.readLine();

                if (name != null && hashedPass != null) {
                    // 1. Add to our "Valid" list
                    validEmails.add(email);

                    // 2. Insert/Update into Database
                    User u = new User(email, name.trim(), hashedPass.trim());
                    DatabaseHelper.insertUser(u);
                }
            }

            // 3. AFTER reading the whole file, remove anyone from DB who wasn't in the list
            DatabaseHelper.removeDeletedUsers(validEmails);

        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    // Hash Logic (Unchanged)
    private String hashPassword(String originalPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATED: Login now queries the Database directly
    public User login(String email, String password) {
        if (email == null || password == null) return null;

        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();
        String inputHash = hashPassword(cleanPassword);

        // Fetch user from DB
        User storedUser = DatabaseHelper.getUser(cleanEmail);

        if (storedUser != null) {
            // Check password hash
            if (storedUser.getPassword().equals(inputHash)) {
                return storedUser;
            }
        }
        return null;
    }

    // UPDATED: Register saves to DB AND File
    public boolean register(String email, String name, String password) {
        if (isInvalid(email) || isInvalid(name) || isInvalid(password)) {
            return false;
        }

        String cleanEmail = email.trim().toLowerCase();

        // Check DB first to see if user exists
        if (DatabaseHelper.getUser(cleanEmail) != null) {
            return false; // User already exists
        }

        String securePassword = hashPassword(password.trim());
        User newUser = new User(cleanEmail, name.trim(), securePassword);

        // 1. Save to Database (Relational Database Feature)
        DatabaseHelper.insertUser(newUser);

        // 2. Save to File (File I/O Feature Backup)
        saveUserToFile(newUser);

        return true;
    }

    private boolean isInvalid(String input) {
        return input == null || input.trim().isEmpty();
    }

    private void saveUserToFile(User user) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.println(user.getEmail());
            writer.println(user.getDisplayName());
            writer.println(user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}