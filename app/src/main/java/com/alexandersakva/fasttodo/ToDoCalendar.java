package com.alexandersakva.fasttodo;

import java.util.GregorianCalendar;


public class ToDoCalendar extends GregorianCalendar {

    int daysGreen = 14;
    int daysYellow = 7;
    int daysOrange = 3;
    int daysRed = 1;

    int overdueDays = 3;

    int currentTime = (int)(getTimeInMillis()/1000/60/60/24);

    int endGreen = currentTime + daysGreen;
    int endYellow = currentTime + daysYellow;
    int endOrange = currentTime + daysOrange;
    int endRed = currentTime + daysRed;
    int overdueDate = currentTime - overdueDays;

    public int getEndGreen() {
        return endGreen;
    }

    public int getEndYellow() {
        return endYellow;
    }

    public int getEndOrange() {
        return endOrange;
    }

    public int getEndRed() {
        return endRed;
    }

    public int getCurrentTime() {
        return currentTime;
    }


    public int getOverdueDate() {
        return overdueDate;
    }
}
