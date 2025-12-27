package org.example;
import java.time.LocalTime;

public class Welcome {
    public static void printGreeting(String username){
        LocalTime now = LocalTime.now();
        int time = now.getHour();
        if (time >= 0 && time < 12){
            System.out.println("Good morning, "+username);
        } else if (time >= 12 && time < 17) {
            System.out.println("Good Afternoon, " + username);
        } else {
            System.out.println("Good Evening, " + username);
        }
    }
}
