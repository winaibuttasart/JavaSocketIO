package com.example.nai.javasocketio;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ChangeDataFormat {
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int second;

    public ChangeDataFormat() {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentTime = localCalendar.getTime();
        this.day = localCalendar.get(Calendar.DATE);
        this.month = localCalendar.get(Calendar.MONTH) + 1;
        this.year = localCalendar.get(Calendar.YEAR) + 543;
        this.hour = currentTime.getHours();
        this.minute = currentTime.getMinutes();
        this.second = currentTime.getSeconds();
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
