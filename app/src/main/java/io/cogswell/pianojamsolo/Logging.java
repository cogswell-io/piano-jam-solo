package io.cogswell.pianojamsolo;

/**
 * Logging is a class that contains logging utility methods.
 */
public class Logging {
    private static String assemble(String level, String message) {
        return "[" + TimeFormatter.timeNow() + "] " + level + ": " + message;
    }

    private static void log(String level, String message) {
        if ("ERROR".equals(level))
            LoggerFragment.logError(assemble(level, message));
        else
            LoggerFragment.log(assemble(level, message));
    }

    private static void log(String level, String message, Throwable t) {
        log(level, message + "\n" + ExceptionUtils.formatStackTrace(t));
    }

    /**
     * A method to log an error.
     * @param message The message to log.
     */
    public static void error(String message) { log("ERROR", message); }

    /**
     * A method to log an error with stack trace.
     * @param message The message to log.
     * @param t       The Throwable to trace.
     */
    public static void error(String message, Throwable t) { log("ERROR", message, t); }

    /**
     * A method to log info.
     * @param message The message to log.
     */
    public static void info(String message) { log("INFO", message); }

    /**
     * A method to log info with stack trace.
     * @param message The message to log.
     * @param t       The Throwable to trace.
     */
    public static void info(String message, Throwable t) { log("INFO", message, t); }
}
