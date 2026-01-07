package org.example;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.*;

public class Welcome_Journal {
    // displayName , userEmail are for testing only , after member 1 completed will use his data
    private final String displayName;
    private final String userEmail;
    private final Scanner sc = new Scanner(System.in);
    private static final String JOURNAL = "journals/";

    // Optional callback for returning to SMART JOURNAL FEATURES menu
    private final Runnable onBackToFeatures;

    // Keep old constructor for compatibility
    public Welcome_Journal(String userEmail) {
        this(userEmail, userEmail, null);
    }

    public Welcome_Journal(String userEmail, String displayName) {
        this(userEmail, displayName, null);
    }

    // Preferred constructor
    public Welcome_Journal(String userEmail, String displayName, Runnable onBackToFeatures) {
        this.userEmail = userEmail;
        this.displayName = (displayName == null || displayName.trim().isEmpty()) ? userEmail : displayName.trim();
        this.onBackToFeatures = onBackToFeatures;
        new File(JOURNAL).mkdir();
    }

    public void displayMenu() {
        LocalTime now = LocalTime.now();
        String greeting;

        // Logic based on assignment time rules
        if (now.isBefore(LocalTime.of(12, 0))) {
            greeting = "Good Morning";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }



        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(dtf.format(today));

        System.out.println(greeting + ", " + displayName);

        System.out.println("0. Terminate the program everywhere anytime");
        System.out.println("1. Create, Edit & View Journals");
        System.out.println("2. View Weekly Mood Summary");
        System.out.print("> ");

        int function;
        while (true) {
            String in = sc.nextLine();
            if (in.matches("[0-2]")) {
                function = Integer.parseInt(in);
                break;
            }
            System.out.println("Please enter only 0, 1 or 2");
            System.out.print("> ");
        }

        if (function == 1) {
            showJournalMenu();
        } else if (function == 0) {
            terminateClear();
        }  else {
            WeeklySummary.showSummary(userEmail);
        }
    }
    /**
     * Returns the per-day journal file path in the same format used by handleTodayJournal().
     */
    private File getDailyFile(LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new File(JOURNAL + userEmail + "_" + date.format(fmt) + ".txt");
    }

    private void showDateActions(LocalDate date) {
        File f = getDailyFile(date);
        boolean exists = f.exists() && f.length() > 0;

        System.out.println("\n=== " + date + " ===");
        System.out.println("1. View Journal" + (exists ? "" : " (No entry yet)"));
        System.out.println("2. " + (exists ? "Edit Journal" : "Create Journal"));
        System.out.println("3. Back to Dates");
        System.out.print("> ");

        String choice;
        do {
            choice = sc.nextLine();
            if (!choice.matches("[1-3]")) {
                System.out.println("Please enter only 1 - 3");
                System.out.print("> ");
            }
        } while (!choice.matches("[1-3]"));

        switch (choice) {
            case "1":
                if (exists) {
                    viewDailyJournal(date);
                } else {
                    System.out.println("No journal entry found for this date.");
                    showJournalMenu();
                }
                break;
            case "2":
                editDailyJournal(date);
                break;
            case "3":
                showJournalMenu();
                break;
        }
    }

    private void editDailyJournal(LocalDate date) {
        File file = getDailyFile(date);

        System.out.println((file.exists() && file.length() > 0)
                ? "\nEdit your journal entry for " + date + ":"
                : "\nEnter your journal entry for " + date + ":");
        System.out.print("> ");

        String entry = sc.nextLine();
        System.out.println("Saving. Please wait...");

        String moodData;
        String weatherData;
        try {
            moodData = API.getMood(entry);
            weatherData = API.getWeather();
        } catch (Exception e) {
            moodData = "Analysis unavailable";
            weatherData = "Weather unavailable";
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
            writer.println("Date : " + date);
            writer.println("Content : " + entry);
            writer.println("Weather : " + weatherData);
            writer.println("Mood : " + moodData);
            System.out.println("\nJournal saved successfully!");
        } catch (IOException e) {
            System.out.println("\nAn error occurred while writing to the file.");
        }

        // After save, go back to the actions for this same date
        showDateActions(date);
    }

    private void viewDailyJournal(LocalDate date) {
        File file = getDailyFile(date);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No journal entry found for this date.");
            showJournalMenu();
            return;
        }

        String content = "";
        String weather = "-";
        String mood = "-";

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.toLowerCase().startsWith("content")) {
                    int idx = line.indexOf(':');
                    if (idx >= 0) content = line.substring(idx + 1).trim();
                } else if (line.toLowerCase().startsWith("weather")) {
                    int idx = line.indexOf(':');
                    if (idx >= 0) weather = line.substring(idx + 1).trim();
                } else if (line.toLowerCase().startsWith("mood")) {
                    int idx = line.indexOf(':');
                    if (idx >= 0) mood = line.substring(idx + 1).trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the journal file.");
            showJournalMenu();
            return;
        }

        System.out.println("\n=== Journal Entry for " + date + " ===");
        System.out.println(content);
        System.out.println("\nWeather: " + weather);
        System.out.println("Mood   : " + mood);

        System.out.println("\nPress Enter to go back.");
        System.out.print("> ");
        sc.nextLine();

        showDateActions(date);
    }

    public void showJournalMenu() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("\n=== Journal Dates ===");
        System.out.println("1. " + today.minusDays(3).format(today_format));
        System.out.println("2. " + today.minusDays(2).format(today_format));
        System.out.println("3. " + today.minusDays(1).format(today_format));
        System.out.println("4. " + today.format(today_format) + " (Today)");
        System.out.println("5. Back");

        System.out.println("Select a date first:");
        System.out.print("> ");

        String input;
        int day = 0;
        do {
            input = sc.nextLine();
            if (!input.matches("[1-5]")) {
                System.out.println("\nPlease enter only from 1 - 5.");
                System.out.print("> ");
            }
        } while (!input.matches("[1-5]"));

        day = Integer.parseInt(input);

        if (day == 5) {
            // Back to SMART JOURNAL FEATURES if callback exists, else back to this menu
            if (onBackToFeatures != null) {
                onBackToFeatures.run();
            } else {
                displayMenu();
            }
            return;
        }

        LocalDate selectedDate = today.minusDays(4 - day);
        showDateActions(selectedDate);
    }

    // Keep old methods but route them to new logic
    private void handleTodayJournal(LocalDate date) {
        editDailyJournal(date);
    }

    private void viewTodayJournal(LocalDate date) {
        viewDailyJournal(date);
    }

    private void viewPastJournal(LocalDate date) {
        viewDailyJournal(date);
    }

    private void terminateClear () {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        File todayFile = new File(JOURNAL + userEmail + "_" + today.format(today_format) + ".txt");

        try {
            // Clear Today's File
            if (todayFile.exists()) {
                new FileWriter(todayFile, false).close();
            }

            System.out.println("\nToday's journal data has been cleared.");
        } catch (IOException e) {
            System.out.println("Error occurred while clearing data.");
        }

        System.out.println("Program terminated successfully.");
        System.exit(0);
    }
}
