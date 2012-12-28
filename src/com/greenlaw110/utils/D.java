package com.greenlaw110.utils;

import org.joda.time.DateTime;

import java.util.Date;

public class D {
    public static Date plusDays(Date date, int day) {
        DateTime dt = new DateTime(date);
        dt = dt.plusDays(day);
        return dt.toDate();
    }
    public static Date minusDays(Date date, int day) {
        return plusDays(date, -1 * day);
    }
    public static Date plusMonths(Date date, int month) {
        DateTime dt = new DateTime(date);
        dt = dt.plusMonths(month);
        return dt.toDate();
    }
    public static Date minusMonths(Date date, int month) {
        return plusMonths(date, -1 * month);
    }
}
