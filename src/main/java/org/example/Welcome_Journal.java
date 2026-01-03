package org.example;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.*;

public class Welcome_Journal {
    // displayName , userEmail are for testing only , after member 1 completed will use his data
    private String displayName = "John";
    private String userEmail;
    private String filepeth;
    private Scanner sc = new Scanner(System.in);
    //private PrintWriter out = new PrintWriter(System.out);
    private static final String JOURNAL = "journals/";

    public Welcome_Journal(String userEmail) {
        this.userEmail = userEmail;
        this.filepeth = JOURNAL+userEmail+".txt";
        //this.writer =
        new File(JOURNAL).mkdir();
    }

     static void main() {
        Welcome_Journal welcome = new Welcome_Journal("amy456");
        // this the the previous 3 days file, not same with the today file
        try (PrintWriter writer = new PrintWriter(new FileWriter(welcome.filepeth, true))) {
             writer.println("Date : 2025-12-25");
             writer.println("Content : Today is the first journal of John");
             writer.println("Weather : A");
             writer.println("Mood : POSITIVE");
             writer.println("Date : 2025-12-26");
             writer.println("Content : Today is the second journal of John");
             writer.println("Weather : B");
             writer.println("Mood : NEGATIVE");
             writer.println("Date : 2025-12-27");
             writer.println("Content : Today is the third journal of John");
             writer.println("Weather : C");
             writer.println("Mood : POSITIVE");

             System.out.println("Successfully wrote to file.");
         } catch (IOException e) {
             System.out.println("An error occurred while writing to the file.");
             //e.printStackTrace();
         }
        welcome.displayMenu();

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

        Scanner sc = new Scanner(System.in);
        System.out.println("0. Terminate the program everywhere anytime");
        System.out.println("1. Create, Edit & View Journals");
        System.out.println("2. View Weekly Mood Summary");
        System.out.print("> ");

        int function=0;
        do {
            if (sc.hasNextInt()){
                function = sc.nextInt();
                if (function !=1 && function !=2 && function !=0)
                    System.out.println("Please enter only 1 or 2");
            } else {
                System.out.println("Please enter only 1 or 2");
                sc.nextLine();
                function=0;
            }
        } while (function !=1 && function !=2 && function!=0);

        if (function == 1) {
            showJournalMenu();
        } else if (function == 0) {
            terminateClear();
        }
    }
    public void showJournalMenu() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Define the filename for today's journal based on email and date
        File todayFile = new File(JOURNAL + userEmail + "_" + today.format(today_format) + ".txt");

        System.out.println("\n=== Journal Dates ===");

        // Display list of past journal dates
        System.out.println("1. " + today.minusDays(3).format(today_format));
        System.out.println("2. " + today.minusDays(2).format(today_format));
        System.out.println("3. " + today.minusDays(1).format(today_format));
        System.out.println("4. " + today.format(today_format) + " (Today)");

        System.out.println("Select a date to view journal, or " + ((todayFile.exists() && todayFile.length() >0 )? "edit" : "create") + " a new journal for today:");
        System.out.print("> ");

        String input;
        boolean valid = false;
        int day = 0;
        do {
            input = sc.nextLine();
            // Check if the input is purely digits using Regex
            // Matches exactly one digit from the set [0, 1, 2, 3, 4]
            if (input.matches("[0-4]")){
                day = Integer.parseInt(input);
                valid = true;
            }
            else{
                System.out.println("\nPlease enter only from 1 - 4. No other input other than integer !");
                System.out.print(">");
            }
        } while (!valid);

        if (day == 4) {
            handleTodayJournal(today);
        } else if (day >=1 && day <= 3){
            viewPastJournal(today.minusDays(4 - day));
        } else
            terminateClear();

    }

    private void handleTodayJournal(LocalDate date) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        File todayFile = new File(JOURNAL + userEmail + "_" + today.format(today_format) + ".txt");

        // Prompt changes based on whether journal exists
        if (todayFile.exists() && todayFile.length() ==0) {
            System.out.println("\nEnter your journal entry for " + date + ":");
        } else {
            System.out.println("\nEdit your journal entry for " + date + ":");
        }

        System.out.print("> ");
        String entry = sc.nextLine();

        String moodData ;
        String weatherData;

        try {
            // These calls run every time you "Edit", so the mood will change based on 'entry'
            moodData = API.getMood(entry);
            weatherData = API.getWeather();
        } catch (Exception e) {
            // If you edit too many times too fast, the API might block you (HTTP 429)
            moodData = "Analysis unavailable (Rate limit)";
            weatherData = "Weather unavailable";
            System.out.println("API Error: ");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(todayFile,false))) {
            writer.println("Date : "+date);
            writer.println("Content : "+entry);
            writer.println("Weather : "+ weatherData);
            writer.println("Mood : "+moodData);

            System.out.println("\nSuccessfully wrote to file.");
        } catch (IOException e) {
            System.out.println("\nAn error occurred while writing to the file.");
        }


        //String weather = API.getWeather();
        System.out.println("Journal saved successfully!");
        System.out.println(weatherData);

        System.out.println("\nWould you like to:");
        System.out.println("1. View Journal\n" + "2. Edit Journal\n" + "3. Back to Dates");
        System.out.print(">");

        String inputUpdate;
        boolean valid = false;
        int update = 0;
        do {
            inputUpdate = sc.nextLine();
            // Check if the input is purely digits using Regex
            // Matches exactly one digit from the set [0, 1, 2, 3]
            if (inputUpdate.matches("[0-3]")){
                update = Integer.parseInt(inputUpdate);
                valid = true;
            }
            else{
                System.out.println("\nPlease enter only from 1 - 3. No other input other than integer !");
            }
        } while (!valid);
        String viewDayInput;
        boolean viewDayValid = false;
        int viewDay = 0;
        if (update == 1) {
            // Show date list before selecting a date to view
            System.out.println("\n=== Journal Dates ===");
            System.out.println("1. " + today.minusDays(3).format(today_format));
            System.out.println("2. " + today.minusDays(2).format(today_format));
            System.out.println("3. " + today.minusDays(1).format(today_format));
            System.out.println("4. " + today.format(today_format) + " (Today)");
            System.out.println("Select a date to view journal.");
            System.out.print(">");
            do {
                viewDayInput = sc.nextLine();
                if (viewDayInput.matches("[1-4]")){
                    viewDay = Integer.parseInt(viewDayInput);
                    viewDayValid = true;
                }
                else{
                    System.out.println("\nPlease enter only from 1 - 4 (day). No other input other than integer !");
                }
            } while (!viewDayValid);
            if (viewDay !=4) {
                viewPastJournal(today.minusDays(4-viewDay));
            } else {
                viewTodayJournal(today);
            }
        } else if (update == 2) {
            handleTodayJournal(today);
        } else if (update == 3) {
            showJournalMenu();
        } else
            terminateClear();
    }

    private void viewTodayJournal(LocalDate date) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        File todayFile = new File(JOURNAL + userEmail + "_" + today.format(today_format) + ".txt");

        if (!todayFile.exists()) {
            System.out.println("No journal entry found for this date.");
            return;
        }
        String targetDate = "Date : " + date;
        System.out.println(targetDate);

        try (Scanner reader = new Scanner(todayFile)) {
            boolean foundTodayDate = false;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                // Check if this line is the date we are looking for
                if (line.trim().contains("Date") && line.contains(date.toString())) {
                    foundTodayDate = true;
                    if (reader.hasNextLine()) {
                        String contentLine = reader.nextLine();
                        String contentOnly = contentLine.substring(10);
                        System.out.println("\n=== Journal Entry for " + date + " ===");
                        System.out.println(contentOnly);
                        break;
                    }
                }
            }
            if (!foundTodayDate) {
                System.out.println("No entry found for " + date);
            }
            System.out.println("\nPress Enter to go back.");
            System.out.print(">");
            sc.nextLine();
            showJournalMenu();

        } catch (IOException e) {
            System.out.println("Error reading the journal file.");
        }

    }

    private void viewPastJournal(LocalDate date) {
        File file = new File(JOURNAL + userEmail + ".txt");
        if (!file.exists()) {
            System.out.println("No journal entry found for this date.");
            return;
        }
        String targetDate = "Date : " + date;
        System.out.println(targetDate);

        try (Scanner reader = new Scanner(file)) {
            boolean foundDate = false;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                // Check if this line is the date we are looking for
                if (line.trim().equals(targetDate)) {
                    foundDate = true;
                    if (reader.hasNextLine()) {
                        String contentLine = reader.nextLine();
                        String contentOnly = contentLine.substring(10);
                        System.out.println("\n=== Journal Entry for " + date + " ===");
                        System.out.println(contentOnly);
                        break;
                    }
                }
            }
            if (!foundDate) {
                System.out.println("No entry found for " + date);
            }
            System.out.println("\nPress Enter to go back.");
            System.out.print(">");
            sc.nextLine();
            showJournalMenu();

        } catch (IOException e) {
            System.out.println("Error reading the journal file.");
        }
    }

    private void terminateClear () {
        LocalDate today = LocalDate.now();
        DateTimeFormatter today_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        File todayFile = new File(JOURNAL + userEmail + "_" + today.format(today_format) + ".txt");
        File historyFile = new File(JOURNAL + userEmail + ".txt");

        try {
            // Clear Today's File
            if (todayFile.exists()) {
                new FileWriter(todayFile, false).close();
            }

            // Clear History File
            if (historyFile.exists()) {
                new FileWriter(historyFile, false).close();
            }

            System.out.println("\nAll journal data has been cleared.");
        } catch (IOException e) {
            System.out.println("Error occurred while clearing data.");
        }

        System.out.println("Program terminated successfully.");
        System.exit(0);
    }

}
