package io.cogswell.pianojamsolo;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * ExceptionUtils is the class that contains utility methods
 * dealing with exceptions.
 */
public class ExceptionUtils {
    /**
     * A method for formatting stack traces.
     * @param t   The throwable to format.
     * @return    The string containing the stack trace.
     */
    public static String formatStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        return sw.toString();
    }
}
