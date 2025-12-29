package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String FILE_PATH = "UserData.txt";
    private List<User> users = new ArrayList<>();

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        // Use UTF_8 explicitly when reading the file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String email;
            while ((email = reader.readLine()) != null) {
                email = email.trim();
                if (email.isEmpty()) continue; // Skip blank lines

                String name = reader.readLine();
                String hashedPass = reader.readLine();

                if (name != null && hashedPass != null) {
                    users.add(new User(email, name.trim(), hashedPass.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private String hashPassword(String originalPassword) {
        try {
            // Ensure we use UTF-8 explicitly to match the registration hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));

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

    public User login(String email, String password) {
        if (email == null || password == null) return null;

        // Use .trim() and .toLowerCase() to match registration
        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();

        String inputHash = hashPassword(cleanPassword);

        for (User u : users) {
            // Compare against cleaned stored data
            String storedEmail = u.getEmail().trim().toLowerCase();
            String storedHash = u.getPassword().trim();

            if (storedEmail.equals(cleanEmail) && storedHash.equals(inputHash)) {
                return u;
            }
        }
        return null;
    }

    public boolean register(String email, String name, String password) {
        if (isInvalid(email) || isInvalid(name) || isInvalid(password)) {
            return false;
        }

        // IMPORTANT: Save email as lowercase so login works regardless of caps
        String securePassword = hashPassword(password.trim());
        User newUser = new User(email.trim().toLowerCase(), name.trim(), securePassword);

        users.add(newUser);
        saveUserToFile(newUser);
        return true;
    }

    private boolean isInvalid(String input) {
        return input == null || input.trim().isEmpty();
    }

    private void saveUserToFile(User user) {
        // Use OutputStreamWriter to force UTF-8 when saving
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.println(user.getEmail());
            writer.println(user.getDisplayName());
            writer.println(user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}