package org.example;
import java.time.LocalTime;

public class Welcome {
    public static void printGreeting(String username){
        // Get the current time from the computer's clock
        LocalTime now = LocalTime.now();

        // Extract just the Hour (0 to 23)
        int time = now.getHour();

        // Decide which greeting to use
        if (time >= 0 && time < 12){
            System.out.println("Good morning, "+username);
        } else if (time >= 12 && time < 17) {
            System.out.println("Good Afternoon, " + username);
        } else {
            System.out.println("Good Evening, " + username);
        }
    }
}
