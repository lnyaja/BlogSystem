package org.sun;

import java.util.Calendar;

public class TestDate {
    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        instance.set(2090,11,1);
        long timeInMillis = instance.getTimeInMillis();
        int length = String.valueOf(timeInMillis).length();
        System.out.println("length == > " + length);
        System.out.println("timeInMillis == >" + timeInMillis);
    }
}
