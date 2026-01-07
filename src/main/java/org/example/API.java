package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class API {

    // NOTE: Weather source is Malaysia gov forecast API.
    // NOTE: Mood source is HuggingFace sentiment model.

    private static final String getURL = "https://api.data.gov.my/weather/forecast/?contains=WP%20Kuala%20Lumpur@location__location_name&sort=date&limit=1";
    private static final String postURL = "https://router.huggingface.co/hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";

    // Small helper: escape text so it's safe inside a JSON string
    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': out.append("\\\\"); break;
                case '"': out.append("\\\""); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    private static boolean isDebugEnabled() {
        // Enable by setting API_DEBUG=true in environment variables or .env
        // (System env is checked first.)
        String v = System.getenv("API_DEBUG");
        if (v != null) return "true".equalsIgnoreCase(v.trim());

        try {
            Map<String, String> env = EnvLoader.loadEnv(".env");
            v = env.get("API_DEBUG");
            return v != null && "true".equalsIgnoreCase(v.trim());
        } catch (Exception ignore) {
            return false;
        }
    }

    // Very small local fallback so obvious cases work even if API fails/rate-limits
    private static String fallbackMood(String journalText) {
        if (journalText == null) return "NEUTRAL";
        String t = journalText.toLowerCase(Locale.ROOT);

        // normalize common apostrophes/shortcuts
        t = t.replace("i'm", "im");
        t = t.replace("can't", "cant");
        t = t.replace("won't", "wont");

        // Negative keywords (expanded)
        if (t.contains("sad") || t.contains("depressed") || t.contains("unhappy") || t.contains("angry") || t.contains("upset")
                || t.contains("hate") || t.contains("bad") || t.contains("tired") || t.contains("stress") || t.contains("stressed")
                || t.contains("cry") || t.contains("crying") || t.contains("tears") || t.contains("terrible") || t.contains("broken")
                || t.contains("lonely") || t.contains("hurt") || t.contains("pain") || t.contains("down")) {
            return "NEGATIVE";
        }

        // Positive keywords
        if (t.contains("happy") || t.contains("great") || t.contains("good") || t.contains("fun") || t.contains("love")
                || t.contains("excited") || t.contains("awesome") || t.contains("nice") || t.contains("amazing")) {
            return "POSITIVE";
        }

        return "NEUTRAL";
    }

    /**
     * Sends a GET request to the specified API URL.
     *
     * @param apiURL the URL to send the GET request to
     * @return the response body as a String
     * @throws Exception if the request fails
     */
    public static String get(String apiURL) throws Exception {
        URL url = new URL(apiURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set HTTP method and headers
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Check for successful response
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("GET failed. HTTP error code: " + conn.getResponseCode());
        }

        // Read response
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        conn.disconnect();
        return sb.toString();
    }

    /**
     * Sends a POST request with JSON body and Bearer token authentication.
     *
     * @param apiURL      the URL to send the POST request to
     * @param bearerToken the bearer token for Authorization header
     * @param jsonBody    the JSON payload as a string
     * @return the response body as a String
     * @throws Exception if the request fails
     */
    public static String post(String apiURL, String bearerToken, String jsonBody) throws Exception {
        URL url = new URL(apiURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set HTTP method and headers
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + bearerToken);

        // Enable sending body
        conn.setDoOutput(true);

        // Write request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Check for success
        int responseCode = conn.getResponseCode();
        if (responseCode != 200 && responseCode != 201) {
            throw new RuntimeException("POST failed. HTTP error code: " + responseCode);
        }

        // Read response
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder sb = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            sb.append(responseLine.trim());
        }

        conn.disconnect();
        return sb.toString();
    }

    // Optional manual test runner (only used if you run `java org.example.API` directly)
    public static void main(String[] args) {
        System.out.println("Weather now: " + getWeather());
        System.out.println("Mood test (happy): " + getMood("I feel great today"));
        System.out.println("Mood test (sad)  : " + getMood("I feel sad and want to cry"));
    }

    // --- NEW METHOD: Get the Weather String ---
    public static String getWeather() {
        try {
            // Add a cache-buster to reduce chance of stale/cached responses
            String cacheBustedUrl = getURL + "&t=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()), StandardCharsets.UTF_8);
            String response = get(cacheBustedUrl);

            String targetLabel = "\"summary_forecast\":\"";
            int startIndex = response.indexOf(targetLabel);
            if (startIndex == -1) {
                return "Weather data unavailable";
            }
            startIndex = startIndex + targetLabel.length();

            int endIndex = response.indexOf("\"", startIndex);
            if (endIndex == -1 || endIndex <= startIndex) {
                return "Weather data unavailable";
            }

            String weather = response.substring(startIndex, endIndex).trim();
            return weather.isEmpty() ? "Weather data unavailable" : weather;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching weather";
        }
    }

    // --- NEW METHOD: Get the Mood (Positive/Negative) ---
    public static String getMood(String journalText) {
        String local = fallbackMood(journalText);
        boolean debug = isDebugEnabled();

        try {
            Map<String, String> env = EnvLoader.loadEnv(".env");
            String token = env.get("BEARER_TOKEN");
            if (token == null) {
                if (debug) System.out.println("[API.getMood] No token; using fallback=" + local);
                return local;
            }
            token = token.trim();
            if (token.isEmpty()) {
                if (debug) System.out.println("[API.getMood] Empty token; using fallback=" + local);
                return local;
            }

            String safeText = jsonEscape(journalText);
            String jsonBody = "{\"inputs\":\"" + safeText + "\"}";

            String response = post(postURL, token, jsonBody);

            String targetLabel = "\"label\":\"";
            int startIndex = response.indexOf(targetLabel);
            if (startIndex == -1) {
                if (debug) System.out.println("[API.getMood] No label in response; using fallback=" + local + " response=" + response);
                return local;
            }

            startIndex = startIndex + targetLabel.length();
            int endIndex = response.indexOf("\"", startIndex);
            if (endIndex == -1) {
                if (debug) System.out.println("[API.getMood] Bad label parse; using fallback=" + local + " response=" + response);
                return local;
            }

            String label = response.substring(startIndex, endIndex).trim().toUpperCase(Locale.ROOT);
            String normalized;
            if (label.contains("NEG")) normalized = "NEGATIVE";
            else if (label.contains("POS")) normalized = "POSITIVE";
            else normalized = local;

            // If the AI says POSITIVE but our fallback sees strong negative cues, trust the fallback.
            // This avoids obvious misclassifications like "...to cry".
            if ("POSITIVE".equals(normalized) && "NEGATIVE".equals(local)) {
                normalized = "NEGATIVE";
                if (debug) {
                    System.out.println("[API.getMood] Overriding AI POSITIVE due to strong negative keywords; using NEGATIVE");
                }
            }

            if (debug) {
                System.out.println("[API.getMood] AI label=" + label + " normalized=" + normalized + " fallback=" + local);
            }

            return normalized;

        } catch (Exception e) {
            if (debug) {
                System.out.println("[API.getMood] Exception calling API; using fallback=" + local + " error=" + e.getMessage());
            }
            return local;
        }
    }
}
