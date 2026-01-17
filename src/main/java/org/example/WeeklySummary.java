package org.example;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;


public class WeeklySummary {
    // Welcome_Journal writes per-day files into "journals/<email>_yyyy-MM-dd.txt"
    private static final String FOLDER_PATH = "journals";

    /**
     * Backwards-compatible: show summary without a user context.
     * (Will display "-" unless the default file format happens to exist.)
     */
    public static void showSummary() {
        showSummary(null);
    }

    /**
     * Shows the last 7 days of WEATHER + MOOD for a specific user.
     *
     * @param userEmail the logged-in user's email (used in filename). If null/blank, no files will be found.
     */
    public static void showSummary(String userEmail) {
        // Print the Table Header
        System.out.println("=======================================================================");
        System.out.println("                        WEEKLY MOOD & WEATHER                           ");
        System.out.println("=======================================================================");
        System.out.printf("%-15s | %-30s | %-15s%n", "DATE", "WEATHER", "MOOD");
        System.out.println("-----------------------------------------------------------------------");

        // Get today's date
        LocalDate today = LocalDate.now();

        // Loop 7 times (from 6 days ago -> to 0 days ago/today)
        for (int i = 6; i >= 0; i--) {
            // Calculate the date for "i days ago"
            LocalDate targetDate = today.minusDays(i);

            // Go find the data for that specific day
            String[] data = getJournalData(userEmail, targetDate);

            String weather = data[0];
            String mood = data[1];

            // Print the row in the table
            System.out.printf("%-15s | %-30s | %-15s%n", targetDate, weather, mood);
        }
        System.out.println("=======================================================================");
    }

    // Helper to read one specific file
    private static String[] getJournalData(String userEmail, LocalDate date) {
        // Default values if the file is missing
        String weather = "-";
        String mood = "-";

        // If no user is logged in, return empty data
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return new String[]{weather, mood};
        }

        // Construct the exact filename we are looking for
        String safeEmail = userEmail.trim();
        String filename = FOLDER_PATH + "/" + safeEmail + "_" + date.toString() + ".txt";
        File file = new File(filename);

        // Check if the file actually exists
        if (file.exists() && file.length() > 0) {
            try (Scanner sc = new Scanner(file)) {
                // Scan through the file line by line
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();

                    // Support BOTH formats:
                    // - "Weather: xxx" (JournalEntries samples)
                    // - "Weather : xxx" (Welcome_Journal writer)
                    if (line.toLowerCase().startsWith("weather")) {
                        int idx = line.indexOf(':');
                        if (idx >= 0 && idx + 1 < line.length()) {
                            weather = line.substring(idx + 1).trim();
                        }
                    } else if (line.toLowerCase().startsWith("mood")) {
                        int idx = line.indexOf(':');
                        if (idx >= 0 && idx + 1 < line.length()) {
                            mood = line.substring(idx + 1).trim();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error reading file: " + filename);
            }
        }

        // Return the found data
        return new String[]{weather, mood};
    }
}