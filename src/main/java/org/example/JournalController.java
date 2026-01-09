package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/journal")
@CrossOrigin(origins = "http://localhost:5173")
public class JournalController {

    private static final String JOURNAL_DIR = "journals/";

    public JournalController() {
        new File(JOURNAL_DIR).mkdirs();
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getEntries(@RequestParam String email) {
        String normalizedEmail = normalizeEmail(email);
        List<Map<String, Object>> entries = new ArrayList<>();

        File journalDir = new File(JOURNAL_DIR);
        File[] files = journalDir.listFiles((dir, name) ->
            name.toLowerCase(Locale.ROOT).startsWith(normalizedEmail + "_") && name.toLowerCase(Locale.ROOT).endsWith(".txt")
        );

        if (files != null) {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    String date = "";
                    String title = "";
                    String weather = "";
                    String mood = "";

                    StringBuilder content = new StringBuilder();
                    boolean inContent = false;

                    String raw;
                    while ((raw = reader.readLine()) != null) {
                        String line = raw.trim();
                        String lower = line.toLowerCase(Locale.ROOT);

                        if (lower.startsWith("date")) {
                            inContent = false;
                            int idx = line.indexOf(':');
                            if (idx >= 0) date = line.substring(idx + 1).trim();
                        } else if (lower.startsWith("title")) {
                            inContent = false;
                            int idx = line.indexOf(':');
                            if (idx >= 0) title = line.substring(idx + 1).trim();
                        } else if (lower.startsWith("content")) {
                            inContent = true;
                            int idx = raw.indexOf(':');
                            if (idx >= 0) {
                                content.append(raw.substring(idx + 1).trim());
                            }
                        } else if (lower.startsWith("weather")) {
                            inContent = false;
                            int idx = line.indexOf(':');
                            if (idx >= 0) weather = line.substring(idx + 1).trim();
                        } else if (lower.startsWith("mood")) {
                            inContent = false;
                            int idx = line.indexOf(':');
                            if (idx >= 0) mood = line.substring(idx + 1).trim();
                        } else if (inContent) {
                            // Multi-line journal content
                            if (!content.isEmpty()) content.append("\n");
                            content.append(raw);
                        }
                    }

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", file.getName().replace(".txt", ""));
                    entry.put("date", date);
                    entry.put("title", title);
                    entry.put("content", content.toString());
                    entry.put("weather", weather);
                    entry.put("mood", mood);
                    entries.add(entry);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Sort by date descending if possible
        entries.sort((a, b) -> {
            String da = Objects.toString(a.get("date"), "");
            String db = Objects.toString(b.get("date"), "");
            return db.compareTo(da);
        });

        return ResponseEntity.ok(entries);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveEntry(@RequestBody JournalEntry entry) {
        try {
            String normalizedEmail = normalizeEmail(entry.getEmail());
            entry.setEmail(normalizedEmail);

            String content = entry.getContent() == null ? "" : entry.getContent();
            String title = entry.getTitle() == null ? "" : entry.getTitle().trim();

            // Always return *some* value
            String moodData = API.getMood(content);
            String weatherData = API.getWeather();

            String filename = JOURNAL_DIR + normalizedEmail + "_" + entry.getDate() + ".txt";
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename, false), StandardCharsets.UTF_8))) {
                writer.println("Date : " + entry.getDate());
                writer.println("Title : " + title);
                writer.println("Content : " + content);
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
    private String title;
    private String content;

    public JournalEntry() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
