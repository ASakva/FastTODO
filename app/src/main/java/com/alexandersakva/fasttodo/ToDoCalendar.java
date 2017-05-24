package com.alexandersakva.fasttodo;

import java.util.GregorianCalendar;


public class ToDoCalendar extends GregorianCalendar {

    public int getHoursGreen() {
        return hoursGreen;
    }

    public int getHoursYellow() {
        return hoursYellow;
    }

    public int getHoursOrange() {
        return hoursOrange;
    }

    public int getHoursRed() {
        return hoursRed;
    }

    private int hoursGreen = 10*24;
    private int hoursYellow = 5*24;
    private int hoursOrange = 48;
    private int hoursRed = 12;

    int overdueTime = 72;

    int currentTime = (int)(getTimeInMillis()/1000/60/60);

    private int endGreen = currentTime + hoursGreen;
    private int endYellow = currentTime + hoursYellow;
    private int endOrange = currentTime + hoursOrange;
    private int endRed = currentTime + hoursRed;
    private int overdueDate = currentTime - overdueTime;

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
