package io.cogswell.pianojamsolo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeFormatter is a class that implements utilities for formatting time strings.
 */
public class TimeFormatter {
    private static final ThreadLocal<SimpleDateFormat> isoFormatters = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> timeFormatters = new ThreadLocal<>();

    private static SimpleDateFormat getIsoFormatter() {
        SimpleDateFormat isoFormatter = isoFormatters.get();

        if (isoFormatter == null) {
            isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            isoFormatters.set(isoFormatter);
        }

        return isoFormatter;
    }

    private static SimpleDateFormat getTimeFormatter() {
        SimpleDateFormat timeFormatter = timeFormatters.get();

        if (timeFormatter == null) {
            timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
            timeFormatters.set(timeFormatter);
        }

        return timeFormatter;
    }

    /**
     * A method to format an ISO string representing the current date time.
     * @return The string representing the current date time.
     */
    public static String isoNow() {
        return getIsoFormatter().format(new Date());
    }

    /**
     * A method to format string representing the current time.
     * @return The string representing the current time.
     */
    public static String timeNow() {
        return getTimeFormatter().format(new Date());
    }

}