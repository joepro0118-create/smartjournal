package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/journal")
@CrossOrigin(origins = "http://localhost:5173")
public class JournalController {

    private static final String JOURNAL_DIR = "journals/";

    public JournalController() {
        new File(JOURNAL_DIR).mkdirs();
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getEntries(@RequestParam String email) {
        List<Map<String, Object>> entries = new ArrayList<>();
        File journalDir = new File(JOURNAL_DIR);
        File[] files = journalDir.listFiles((dir, name) ->
            name.startsWith(email + "_") && name.endsWith(".txt")
        );

        if (files != null) {
            for (File file : files) {
                try (Scanner scanner = new Scanner(file)) {
                    String date = "";
                    String content = "";
                    String weather = "";
                    String mood = "";

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        if (line.toLowerCase().startsWith("date")) {
                            int idx = line.indexOf(':');
                            if (idx >= 0) date = line.substring(idx + 1).trim();
                        } else if (line.toLowerCase().startsWith("content")) {
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

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", file.getName().replace(".txt", ""));
                    entry.put("date", date);
                    entry.put("content", content);
                    entry.put("weather", weather);
                    entry.put("mood", mood);
                    entries.add(entry);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.ok(entries);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveEntry(@RequestBody JournalEntry entry) {
        try {
            String moodData = API.getMood(entry.getContent());
            String weatherData = API.getWeather();

            String filename = JOURNAL_DIR + entry.getEmail() + "_" + entry.getDate() + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
                writer.println("Date : " + entry.getDate());
                writer.println("Content : " + entry.getContent());
                writer.println("Weather : " + weatherData);
                writer.println("Mood : " + moodData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Journal saved successfully");
            response.put("weather", weatherData);
            response.put("mood", moodData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to save journal: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

class JournalEntry {
    private String email;
    private String date;
    private String content;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

