package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class API {

    /**
     * Sends a GET request to the specified API URL.
     * 
     * @param apiURL the URL to send the GET request to
     * @return the response body as a String
     * @throws Exception if the request fails
     */

    private static final String getURL = "https://api.data.gov.my/weather/forecast/?contains=WP%20Kuala%20Lumpur@location__location_name&sort=date&limit=1";
    private static final String postURL = "https://router.huggingface.co/hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";

    // Cache weather for current session
    private static String cachedWeather = null;

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

    // Example usage
    public static void main(String[] args) {
        System.out.println("--- MEMBER 3 FINAL CHECK ---");

        // 1. Test Weather
        System.out.println("1. Testing Weather...");
        String weather = getWeather();
        System.out.println("   [RESULT] " + weather);

        // 2. Test Mood
        System.out.println("\n2. Testing Mood AI...");
        // Let's pretend the user wrote a happy diary entry
        String sampleJournal = "I learned how to code today and it was so much fun!";
        String mood = getMood(sampleJournal);

        System.out.println("   [INPUT] " + sampleJournal);
        System.out.println("   [RESULT] " + mood);
    }

    // --- NEW METHOD: Get the Weather String ---
    public static String getWeather() {
        if (cachedWeather != null) {
            return cachedWeather; // reuse cached value
        }
        try {
            // 1. CALL THE API
            // This grabs the raw text from the government website
            String response = get(getURL);

            // 2. FIND THE START
            // We are looking for the phrase: "summary_forecast":"
            String targetLabel = "\"summary_forecast\":\"";
            int startIndex = response.indexOf(targetLabel);

            // Safety check: Did we find it?
            if (startIndex == -1) {
                return "Weather data unavailable";
            }

            // Move the index to the END of the label so we start reading the actual weather
            startIndex = startIndex + targetLabel.length();

            // 3. FIND THE END
            // The weather description ends at the next quote mark (")
            int endIndex = response.indexOf("\"", startIndex);

            // 4. CUT IT OUT
            // Extract the text between the start and end
            //String weather = response.substring(startIndex, endIndex);

            cachedWeather = response.substring(startIndex, endIndex); // store in cache
            return cachedWeather;

            //return weather;

        } catch (Exception e) {
            // If anything breaks (no internet, bad token), print error
            e.printStackTrace();
            return "Error fetching weather";
        }
    }

    // --- NEW METHOD: Get the Mood (Positive/Negative) ---
    public static String getMood(String journalText) {
        try {
            // 1. PREPARE THE DATA
            // Load the env map to get the token
            Map<String, String> env = EnvLoader.loadEnv(".env");
            String token = env.get("BEARER_TOKEN");

            // Format the user's text into JSON
            String jsonBody = "{\"inputs\": \"" + journalText + "\"}";

            // 2. CALL THE API
            // Use the postURL variable we moved to the top
            String response = post(postURL, token, jsonBody);

            // 3. FIND THE LABEL
            // The assignment says the highest score is always FIRST.
            String targetLabel = "\"label\":\"";
            int startIndex = response.indexOf(targetLabel);

            if (startIndex == -1) return "UNKNOWN";

            // Move index to start of the word
            startIndex = startIndex + targetLabel.length();

            // Find the end of the word
            int endIndex = response.indexOf("\"", startIndex);

            // 4. CUT IT OUT
            return response.substring(startIndex, endIndex);

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
