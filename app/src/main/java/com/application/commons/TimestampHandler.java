package com.application.commons;


import org.joda.time.DateTime;

/**
 * Created by davide on 09/10/14.
 */
public class TimestampHandler {

    public static long getYesterdayTimestamp(DateTime dateTime) {
        return dateTime.toDateTime().minusDays(1).withTimeAtStartOfDay().getMillis();
    }

    public static long getOneWeekAgoTimestamp(DateTime dateTime) {
        return dateTime.toDateTime().minusWeeks(1).withTimeAtStartOfDay().getMillis();
    }

    public static long getTodayTimestamp(DateTime dateTime) {
        return dateTime.toDateTime().withTimeAtStartOfDay().getMillis();
    }

}
