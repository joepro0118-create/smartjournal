package org.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainGUI extends Application {
    private Stage window;
    private UserManager userManager = new UserManager();
    private User currentUser;
    private boolean isDarkMode = false;
    private static final String FOLDER_PATH = "JournalEntries";

    // Inside MainGUI class
    private static final String JOURNAL_FOLDER = "journals/"; //

    /* In your constructor or start method, ensure the directory exists
    new File(JOURNAL_FOLDER).mkdir(); */

    private BorderPane mainLayout;
    private ToggleButton globalThemeToggle;
    private Button globalHomeBtn;

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        window.setTitle("Smart Journaling Project");
        window.setResizable(false);

        // Persistent Controls
        globalThemeToggle = new ToggleButton("🌙 Dark Mode");
        globalThemeToggle.getStyleClass().add("theme-toggle");
        globalThemeToggle.setOnAction(e -> handleThemeToggle());

        // Change the globalHomeBtn text and action
        globalHomeBtn = new Button("🚪 Log Out"); //
        globalHomeBtn.getStyleClass().add("secondary-button");
        globalHomeBtn.setOnAction(e -> {
            currentUser = null; // Clear session
            showLoginScene();    // Return to login page
        });

        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root-pane");

        Button terminateBtn = new Button("( ! ) Clear & Exit");
        terminateBtn.getStyleClass().add("secondary-button");
        terminateBtn.setOnAction(e -> terminateAndClearData());

        // footerBox ensures toggle and home button never move locations
        HBox footerBox = new HBox(20, globalHomeBtn, terminateBtn ,globalThemeToggle);
        footerBox.setPadding(new Insets(15));
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        mainLayout.setBottom(footerBox);

        showLoginScene();

        Scene scene = new Scene(mainLayout, 800, 600);
        applyTheme(scene);
        window.setScene(scene);
        window.show();
    }

    private void handleThemeToggle() {
        isDarkMode = globalThemeToggle.isSelected();
        globalThemeToggle.setText(isDarkMode ? "🌞 Light Mode" : "🌙 Dark Mode");
        applyTheme(window.getScene());
    }

    private void applyTheme(Scene scene) {
        if (scene == null) return;
        scene.getStylesheets().clear();
        String themeFile = isDarkMode ? "/dark.css" : "/light.css";
        try {
            var resource = getClass().getResource(themeFile);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateContent(Parent newContent) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), newContent);
        ft.setFromValue(0.0); ft.setToValue(1.0);
        StackPane container = new StackPane(newContent);
        container.setAlignment(Pos.CENTER);
        mainLayout.setCenter(container);
        ft.play();
    }

    private void showLoginScene() {
        globalHomeBtn.setVisible(false);
        VBox card = new VBox(25);
        card.setMaxSize(400, 520);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("login-card");

        // "Welcome Back" is now a super-header for consistency
        Label title = new Label("Welcome Back");
        title.getStyleClass().add("super-header-label");

        TextField emailInput = new TextField();
        emailInput.setPromptText("Email Address");
        emailInput.getStyleClass().add("modern-input");
        emailInput.setPrefWidth(300);

        // Press Enter to focus password field
        emailInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) card.lookup(".password-field").requestFocus();
        });

        // Password components
        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Password");
        passInput.getStyleClass().addAll("modern-input", "password-field");
        passInput.setPrefWidth(300);

        TextField passVisible = new TextField();
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        passVisible.getStyleClass().add("modern-input");
        passVisible.setPrefWidth(300);

        // The eye button
        Button showPassBtn = new Button("👁");
        showPassBtn.getStyleClass().add("icon-button");

        // Toggle logic: show visible text on press, hide on release
        showPassBtn.setOnMousePressed(e -> {
            passVisible.setText(passInput.getText());
            passVisible.setVisible(true);
            passVisible.setManaged(true);
            passInput.setVisible(false);
            passInput.setManaged(false);
        });
        showPassBtn.setOnMouseReleased(e -> {
            passInput.setVisible(true);
            passInput.setManaged(true);
            passVisible.setVisible(false);
            passVisible.setManaged(false);
        });

        StackPane passStack = new StackPane(passInput, passVisible);
        passStack.setPrefWidth(270); // Ensure the container matches the width

        // CONSISTENT ALIGNMENT: Password and Eye Button in an HBox
        HBox passContainer = new HBox(10);
        passContainer.setAlignment(Pos.CENTER);
        //passContainer.getChildren().addAll(new StackPane(passInput, passVisible), showPassBtn);
        passContainer.getChildren().addAll(passStack, showPassBtn);

        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(150); // Shorter button as requested
        loginBtn.getStyleClass().add("primary-button");


        // ACTION: Login
        Runnable loginAction = () -> {
            currentUser = userManager.login(emailInput.getText(), passInput.getText());
            if (currentUser != null) {
                globalHomeBtn.setVisible(true);
                showJournalScene();
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid email or password.").show();
            }
        };

        loginBtn.setOnAction(e -> loginAction.run());
        // Proceed to Sign In on Enter
        passInput.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) loginAction.run(); });
        passVisible.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) loginAction.run(); });

        Hyperlink regLink = new Hyperlink("Don't have an account? Register here");
        regLink.setOnAction(e -> showRegisterScene());

        card.getChildren().addAll(title, emailInput, passContainer, loginBtn, regLink);
        updateContent(card);
    }

    private void showJournalScene() {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        // Current Date for reference (Today)
        LocalDate today = LocalDate.now();
        // Persistent variable to track which date the user is currently editing
        final LocalDate[] selectedDate = {today};

        Label timeLabel = new Label();
        timeLabel.getStyleClass().add("clock-label");
        setupClock(timeLabel);

        Label welcome = new Label(getGreeting() + ", " + currentUser.getDisplayName());
        welcome.getStyleClass().add("header-label");

        Label weather = new Label("📍 " + API.getWeather());
        weather.getStyleClass().add("weather-display-label");

        VBox header = new VBox(5, timeLabel, welcome, weather);
        header.setAlignment(Pos.CENTER);

        // --- NEW FEATURE: Date Selection Buttons ---
        HBox dateSelectionBox = new HBox(10);
        dateSelectionBox.setAlignment(Pos.CENTER);

        TextArea journalArea = new TextArea();
        journalArea.setWrapText(true);
        journalArea.getStyleClass().add("modern-textarea");
        journalArea.setPrefHeight(250);
        Button saveBtn = new Button(); // Initialize early to update text via helper
        // Initialize Save Button for Today initially
        saveBtn.setText(getJournalData(today)[2].equals("No Record") ? "Create, Analyse & Save Entry" : "Edit, Analyse & Save Entry");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.getStyleClass().add("primary-button");

        saveBtn.setOnAction(e -> {
            // As per your requirement: Use today's weather even for past journals
            String currentWeather = API.getWeather();
            String mood = API.getMood(journalArea.getText());

            // Custom save method to handle the specific selected date
            saveJournalEntryForDate(selectedDate[0], journalArea.getText(), currentWeather, mood);

            saveBtn.setText("Edit, Analyse & Save Entry (" + selectedDate[0] + ")");
            new Alert(Alert.AlertType.INFORMATION, "Journal Saved for " + selectedDate[0]).show();
        });

        // Generate buttons for the last 7 days
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            String labelDate = date.format(DateTimeFormatter.ofPattern("MM-dd"));

            // Logic similar to Welcome_Journal to check if entry exists
            File file = new File(JOURNAL_FOLDER + currentUser.getEmail() + "_" + date + ".txt");
            String status = (file.exists() && file.length() > 0) ? " (Edit)" : " (New)";

            Button dateBtn = new Button(labelDate + status);
            dateBtn.getStyleClass().add("date-select-button");

            dateBtn.setOnAction(e -> {
                selectedDate[0] = date;

                // Fetch data using the date-specific logic
                String[] data = getJournalData(date);

                // Explicitly check for "No Record" to clear the text area
                if (data[2].equals("No Record")) {
                    journalArea.setText(""); // This ensures the box is empty for 'New' days
                } else {
                    journalArea.setText(data[2]); // Load the content for 'Edit' days
                }

                // Update the button text to show the correct date context
                saveBtn.setText(data[2].equals("No Record")
                        ? "Create, Analyse & Save Entry (" + labelDate + ")"
                        : "Edit, Analyse & Save Entry (" + labelDate + ")");

                // Update the Save Button text dynamically
                /*String actionPrefix = (file.exists() && file.length() > 0) ? "Edit" : "Create";
                saveBtn.setText(actionPrefix + ", Analyse & Save Entry (" + labelDate + ")");*/
            });
            dateSelectionBox.getChildren().add(dateBtn);
        }
        layout.getChildren().add(dateSelectionBox);

        VBox journalCard = new VBox(20);
        journalCard.getStyleClass().add("journal-card");
        journalCard.setPadding(new Insets(25));
        journalCard.setMaxWidth(700);


        /* --- DYNAMIC BUTTON LOGIC START --- */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Define the today-specific file path
        File todayFile = new File(JOURNAL_FOLDER + currentUser.getEmail() + "_" + today.format(dtf) + ".txt");

        String[] initialData = getJournalData(selectedDate[0]);
        journalArea.setText(initialData[2].equals("No Record") ? "" : initialData[2]);
        saveBtn.setText(initialData[2].equals("No Record")
                ? "Create, Analyse & Save Entry (" + selectedDate[0].format(DateTimeFormatter.ofPattern("MM-dd")) + ")"
                : "Edit, Analyse & Save Entry (" + selectedDate[0].format(DateTimeFormatter.ofPattern("MM-dd")) + ")");

        // Determine button text based on file existence and length
        String btnText = (todayFile.exists() && todayFile.length() > 0)
                ? "Edit, Analyse & Save Entry"
                : "Create, Analyse & Save Entry";

        /*Button saveBtn = new Button(btnText);*/
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setOnAction(e -> {
            String content = journalArea.getText();
            String currentWeather = API.getWeather();
            String mood = API.getMood(journalArea.getText());
            // selectedDate[0] holds the date from whichever top button was clicked
            saveJournalEntryForDate(selectedDate[0], content, currentWeather, mood);
            // Update the button text immediately after saving
            saveBtn.setText("Edit, Analyse & Save Entry (" + selectedDate[0] + ")");

            // Refresh the date selection buttons to show "(Edit)" instead of "(New)"
            refreshDateSelectionButtons(dateSelectionBox, today, journalArea, saveBtn, selectedDate);

            new Alert(Alert.AlertType.INFORMATION, "Journal saved for " + selectedDate[0]+ "\nMood detected: " + mood).show();        });

        Button summaryBtn = new Button("View Weekly Summary");
        summaryBtn.getStyleClass().add("secondary-button");
        summaryBtn.setOnAction(e -> showSummaryScene());

        journalCard.getChildren().addAll(journalArea, saveBtn);
        layout.getChildren().addAll(header, journalCard, summaryBtn);
        refreshDateSelectionButtons(dateSelectionBox, today, journalArea, saveBtn, selectedDate);
        updateContent(layout);
    }

    private void refreshDateSelectionButtons(HBox container, LocalDate today, TextArea journalArea, Button saveBtn, LocalDate[] selectedDate) {
        container.getChildren().clear();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            String labelDate = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            File file = new File(JOURNAL_FOLDER + currentUser.getEmail() + "_" + date + ".txt");
            String status = (file.exists() && file.length() > 0) ? " (Edit)" : " (New)";
            Button dateBtn = new Button(labelDate + status);
            dateBtn.getStyleClass().add("date-select-button");
            dateBtn.setOnAction(e -> {
                selectedDate[0] = date;
                String[] data = getJournalData(date);
                journalArea.setText(data[2].equals("No Record") ? "" : data[2]);
                saveBtn.setText(data[2].equals("No Record")
                        ? "Create, Analyse & Save Entry (" + labelDate + ")"
                        : "Edit, Analyse & Save Entry (" + labelDate + ")");
            });
            container.getChildren().add(dateBtn);
        }
    }

    private void saveJournalEntryForDate(LocalDate date, String text, String weather, String mood) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = date.format(dtf);

        File file = new File(JOURNAL_FOLDER + currentUser.getEmail() + "_" + date + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
            writer.println("Date : " + dateStr);
            writer.println("Content : " + text);
            writer.println("Weather : " + weather);
            writer.println("Mood : " + mood);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSummaryScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        Label title = new Label("Weekly Summary");
        title.getStyleClass().add("header-label");

        VBox card = new VBox(15);
        card.getStyleClass().add("journal-card");
        card.setPadding(new Insets(25));
        card.setMinWidth(750); // Fixed width to prevent shifting

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints colDate = new ColumnConstraints(130);
        ColumnConstraints colWeather = new ColumnConstraints(320);
        ColumnConstraints colMood = new ColumnConstraints(180);

        colDate.setHalignment(javafx.geometry.HPos.LEFT);
        colWeather.setHalignment(javafx.geometry.HPos.LEFT);
        colMood.setHalignment(javafx.geometry.HPos.LEFT);

        grid.getColumnConstraints().addAll(colDate, colWeather, colMood);

        String[] headers = {"Date", "Weather", "Mood"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.getStyleClass().add("table-header");
            grid.add(h, i, 0);
        }

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            String[] data = getJournalData(date);

            Label d = new Label(date.toString());
            d.getStyleClass().add("table-row-text");

            Label w = new Label(data[0]);
            w.getStyleClass().add("table-row-text");

            Label m = new Label(data[1]);
            m.getStyleClass().add("table-row-text");

            String moodValue = data[1].toUpperCase();
            if (moodValue.contains("POSITIVE")) {
                m.getStyleClass().add("mood-positive");
            } else if (moodValue.contains("NEGATIVE")) {
                m.getStyleClass().add("mood-negative");
            }

            // EXPLICIT PLACEMENT: column first, then row (i + 1 to account for header)
            // This ensures Mood (m) is locked into the 3rd column (index 2)
            grid.add(d, 0, i + 1); // Date column
            grid.add(w, 1, i + 1); // Weather column
            grid.add(m, 2, i + 1); // Mood column
        }

        // ADDED: Back Button inside summary scene
        Button backBtn = new Button("Back to Journal");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> showJournalScene());

        card.getChildren().add(grid);
        layout.getChildren().addAll(title, card, backBtn);
        updateContent(layout);
    }

    private void setupClock(Label label) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            label.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private String getGreeting() {
        LocalTime now = LocalTime.now();
        // Rules: Morning < 12:00, Afternoon < 17:00, else Evening
        if (now.isBefore(LocalTime.of(12, 0))) return "Good Morning";
        if (now.isBefore(LocalTime.of(17, 0))) return "Good Afternoon";
        return "Good Evening";
    }

    private void terminateAndClearData() {
        try {
            File folder = new File(JOURNAL_FOLDER);
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Delete only this user's files
                    if (file.getName().startsWith(currentUser.getEmail() + "_") ||
                            file.getName().equals(currentUser.getEmail() + ".txt")) {
                        file.delete();
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "All journal data for user " + currentUser.getEmail() + " has been cleared.");
            alert.showAndWait();
            currentUser = null; // End session

            showLoginScene(); // Return to login
            } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private String[] getJournalData(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = date.format(dtf);

        // Check for today's specific file first
        File todayFile = new File(JOURNAL_FOLDER + currentUser.getEmail() + "_" + dateStr + ".txt");
        // Then check the general history file
        File historyFile = new File(JOURNAL_FOLDER + currentUser.getEmail() + ".txt");

        String weather = "-", mood = "-", content = "No Record";

        // Logic to check todayFile first, then historyFile
        File target = todayFile.exists() ? todayFile : historyFile;

        if (target.exists()) {
            try (Scanner reader = new Scanner(target)) {
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    if (line.startsWith("Date : ") && line.contains(dateStr)) {
                        while (reader.hasNextLine()) {
                            String nextLine = reader.nextLine();
                            if (nextLine.startsWith("Content : ")) {
                                content = nextLine.substring("Content : ".length()).trim();
                            } else if (nextLine.startsWith("Weather : ")) {
                                weather = nextLine.substring("Weather : ".length()).trim();
                            } else if (nextLine.startsWith("Mood : ")) {
                                mood = nextLine.substring("Mood : ".length()).trim();
                                break; // Stop after mood is found
                            }
                        } break; // Stop after matching date block
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        return new String[]{weather, mood, content};
    }

    private void showRegisterScene() {
        globalHomeBtn.setVisible(false);
        VBox card = new VBox(20);
        card.setMaxSize(400, 560);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("login-card");

        Label title = new Label("Create Account");
        title.getStyleClass().add("super-header-label");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Full Name");
        nameInput.getStyleClass().add("modern-input");

        TextField emailInput = new TextField();
        emailInput.setPromptText("Email Address");
        emailInput.getStyleClass().add("modern-input");

        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Password");
        passInput.getStyleClass().add("modern-input");
        passInput.setPrefWidth(300);

        TextField passVisible = new TextField();
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        passVisible.getStyleClass().add("modern-input");
        passVisible.setPrefWidth(300);

        Button showPassBtn = new Button("👁");
        showPassBtn.getStyleClass().add("icon-button");

        showPassBtn.setOnMousePressed(e -> {
            passVisible.setText(passInput.getText());
            passVisible.setVisible(true);
            passVisible.setManaged(true);
            passInput.setVisible(false);
            passInput.setManaged(false);
        });
        showPassBtn.setOnMouseReleased(e -> {
            passInput.setVisible(true);
            passInput.setManaged(true);
            passVisible.setVisible(false);
            passVisible.setManaged(false);
        });

        StackPane passStack = new StackPane(passInput, passVisible);
        passStack.setPrefWidth(300); // Matches emailInput width

        HBox passContainer = new HBox(10);
        passContainer.setAlignment(Pos.CENTER);
        passContainer.getChildren().addAll(passStack, showPassBtn);
        /* --- PASSWORD VIEW TOGGLE END --- */

        Button regBtn = new Button("Register");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.getStyleClass().add("primary-button");

        regBtn.setOnAction(e -> {
            String name = nameInput.getText();
            String email = emailInput.getText();

            // Get password from passInput (synced via your toggle logic)
            String password = passInput.getText();

            boolean success = userManager.register(name, email, password);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("Account Successfully Registered!");
                alert.showAndWait();
                showLoginScene();
            } else {
                new Alert(Alert.AlertType.ERROR, "Registration failed. Email might already exist.").show();
            }
        });

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setOnAction(e -> showLoginScene());

        card.getChildren().addAll(title, nameInput, emailInput, passContainer, regBtn, backLink);        updateContent(card);
    }

    public static void main(String[] args) { launch(args); }
}