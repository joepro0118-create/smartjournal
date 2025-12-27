package org.example;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;


public class WeeklySummary {
    private static final String FOLDER_PATH = "JournalEntries";

    public static void showSummary() {
        System.out.println("=======================================================================");
        System.out.println("                        WEEKLY MOOD & WEATHER                          ");
        System.out.println("=======================================================================");
        System.out.printf("%-15s | %-30s | %-15s%n", "DATE", "WEATHER", "MOOD");
        System.out.println("-----------------------------------------------------------------------");

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);

            String[] data = getJournalData(targetDate);

            String weather = data[0];
            String mood = data[1];

            System.out.printf("%-15s | %-30s | %-15s%n", targetDate, weather, mood);
        }
        System.out.println("=======================================================================");
    }

    private static String[] getJournalData(LocalDate date) {
        String filename = FOLDER_PATH + "/" + date.toString() + ".txt";
        File file = new File(filename);

        String weather = "No Record";
        String mood = "No Record";

        if (file.exists()) {
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();

                    if (line.startsWith("Weather:")) {
                        weather = line.substring(8).trim();
                    } else if (line.startsWith("Mood:")) {
                        mood = line.substring(5).trim();
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error reading file: " + filename);
            }
        } else {
            weather = "-";
            mood = "-";
        }
        return new String[]{weather, mood};
    }
}