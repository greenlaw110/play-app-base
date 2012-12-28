package com.greenlaw110.utils;

import org.joda.time.DateTime;

import java.util.Date;

public class D {
    public Date plusDays(Date date, int day) {
        DateTime dt = new DateTime(date);
        dt = dt.plusDays(day);
        return dt.toDate();
    }
    public Date minusDays(Date date, int day) {
        return plusDays(date, -1 * day);
    }
    public Date plusMonths(Date date, int month) {
        DateTime dt = new DateTime(date);
        dt = dt.plusMonths(month);
        return dt.toDate();
    }
    public Date minusMonths(Date date, int month) {
        return plusMonths(date, -1 * month);
    }
}
