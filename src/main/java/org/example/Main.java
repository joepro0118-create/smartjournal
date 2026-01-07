package org.example;
import java.util.Scanner;
//import java.io.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Require login before accessing Smart Journal features
        UserManager userManager = new UserManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- SMART JOURNAL SYSTEM ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": {
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = scanner.nextLine();

                    User user = userManager.login(email, pass);
                    if (user != null) {
                        System.out.println("SUCCESS: Welcome back, " + user.getDisplayName());

                        Welcome.printGreeting(user.getDisplayName());

                        final boolean[] backToFeatures = { false };
                        Welcome_Journal welcome = new Welcome_Journal(
                                user.getEmail(),
                                user.getDisplayName(),
                                () -> backToFeatures[0] = true
                        );

                        boolean loggedIn = true;
                        while (loggedIn) {
                            backToFeatures[0] = false;

                            System.out.println("\n--- SMART JOURNAL FEATURES ---");
                            System.out.println("1. Journal");
                            System.out.println("2. Weekly Summary");
                            System.out.println("3. Logout");
                            System.out.print("Choose an option: ");

                            String featureChoice = scanner.nextLine();
                            switch (featureChoice) {
                                case "1":
                                    welcome.showJournalMenu();
                                    // if user pressed Back, just show the features menu again
                                    if (backToFeatures[0]) {
                                        continue;
                                    }
                                    break;
                                case "2":
                                    WeeklySummary.showSummary(user.getEmail());
                                    break;
                                case "3":
                                    System.out.println("You have been logged out.");
                                    loggedIn = false;
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        }
                    } else {
                        System.out.println("FAILED: Invalid email or password.");
                    }
                    break;
                }
                case "2": {
                    System.out.print("Enter New Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Display Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = scanner.nextLine();

                    boolean success = userManager.register(email, name, pass);
                    if (success) {
                        System.out.println("SUCCESS: Account created! You can now log in.");
                    } else {
                        System.out.println("FAILED: Email already exists.");
                    }
                    break;
                }
                case "3":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
