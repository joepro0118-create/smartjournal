package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UserManager {
    // The name of the database file on local
    private static final String FILE_PATH = "UserData.txt";
<<<<<<< HEAD

    public UserManager() {
        // 1. Initialize Database Table
        DatabaseHelper.createTable();

        // 2. Perform File I/O: Load existing text file data into the database
        // This ensures you keep your "Basic Feature" mark for File I/O
        loadUsersAndSyncToDB();
    }

    private void loadUsersAndSyncToDB() {
=======

    // A list in memory to hold all users while the app is running
    private List<User> users = new ArrayList<>();

    public UserManager() {
        loadUsers();
    } // Load existing accounts from the file

    // Reads "UserData.txt" line by line to rebuild the list of users.
    private void loadUsers() {
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
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

                // If we successfully read all 3 parts, create the User object
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
<<<<<<< HEAD

    // Hash Logic (Unchanged)
=======
    // Password Hashing
    // Turns "password123" into "ef92b778bafe771e..."
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
    private String hashPassword(String originalPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
<<<<<<< HEAD
            byte[] encodedhash = digest.digest(originalPassword.getBytes(StandardCharsets.UTF_8));
=======
            byte[] encodedhash = digest.digest(originalPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convert byte code into readable Hexadecimal text
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
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

<<<<<<< HEAD
    // UPDATED: Login now queries the Database directly
=======
    // Login
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
    public User login(String email, String password) {
        if (email == null || password == null) return null;

        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();
<<<<<<< HEAD
=======

        // Hash the password the user just typed
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
        String inputHash = hashPassword(cleanPassword);

        // Fetch user from DB
        User storedUser = DatabaseHelper.getUser(cleanEmail);

<<<<<<< HEAD
        if (storedUser != null) {
            // Check password hash
            if (storedUser.getPassword().equals(inputHash)) {
                return storedUser;
=======
            // If email matches and the password hashes match, login is successful
            if (storedEmail.equals(cleanEmail) && storedHash.equals(inputHash)) {
                return u;
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
            }
        }
        return null;
    }

<<<<<<< HEAD
    // UPDATED: Register saves to DB AND File
=======
    // Register
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a
    public boolean register(String email, String name, String password) {
        // Basic validation check
        if (isInvalid(email) || isInvalid(name) || isInvalid(password)) {
            return false;
        }

<<<<<<< HEAD
        String cleanEmail = email.trim().toLowerCase();
=======
        // Hash the password before saving it
        String securePassword = hashPassword(password.trim());

        // Save email as lowercase so login works regardless of caps
        User newUser = new User(email.trim().toLowerCase(), name.trim(), securePassword);
>>>>>>> 626e67f36662fd300c58e74f0e1a2c4becbaf36a

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

    // Helper to check for bad input
    private boolean isInvalid(String input) {
        return input == null || input.trim().isEmpty();
    }

    // Appends the new user to the end of "UserData.txt"
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