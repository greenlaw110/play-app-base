package org.osgl.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.util.Date;

public class D {
    public static int daysBetween(Date start, Date end) {
        Days d = Days.daysBetween(new DateTime(start), new DateTime(end));
        return d.getDays();
    }
    public static int monthsBetween(Date start, Date end) {
        Months m = Months.monthsBetween(new DateTime(start), new DateTime(end));
        return m.getMonths();
    }
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
