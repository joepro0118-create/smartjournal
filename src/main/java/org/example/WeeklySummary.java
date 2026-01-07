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
        System.out.println("=======================================================================");
        System.out.println("                        WEEKLY MOOD & WEATHER                           ");
        System.out.println("=======================================================================");
        System.out.printf("%-15s | %-30s | %-15s%n", "DATE", "WEATHER", "MOOD");
        System.out.println("-----------------------------------------------------------------------");

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);

            String[] data = getJournalData(userEmail, targetDate);

            String weather = data[0];
            String mood = data[1];

            System.out.printf("%-15s | %-30s | %-15s%n", targetDate, weather, mood);
        }
        System.out.println("=======================================================================");
    }

    private static String[] getJournalData(String userEmail, LocalDate date) {
        String weather = "-";
        String mood = "-";

        if (userEmail == null || userEmail.trim().isEmpty()) {
            return new String[]{weather, mood};
        }

        String safeEmail = userEmail.trim();
        String filename = FOLDER_PATH + "/" + safeEmail + "_" + date.toString() + ".txt";
        File file = new File(filename);

        if (file.exists() && file.length() > 0) {
            try (Scanner sc = new Scanner(file)) {
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

        return new String[]{weather, mood};
    }
}