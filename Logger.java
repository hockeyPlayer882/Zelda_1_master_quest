/**
 * Logger class for Velocity. Allows easier reading of error messages and allows hiding of lower
 * priority events.
 */
public class Logger {
    /**
     * Allows all logging messages through.
     */
    public static final int LOG_DEBUG = 0;

    /**
     * Allows warnings and errors through, but suppresses info.
     */
    public static final int LOG_WARN = 1;

    /**
     * Only allows errors through.
     */
    public static final int LOG_ERROR = 2;

    /**
     * Current log level. Ranges from 0-.
     */
    public static int level = 0;

    /**
     * Log a message to the console.
     * 
     * @param module The logging module.
     * @param message The message to print.
     */
    public static void log(String module, String message) {
        if (level > 0) return;
        System.out.println("\033[32mI [" + module + "]: " + message + "\033[0m");
    }

    /**
     * Log a warning message to the console.
     * 
     * @param module The logging module.
     * @param message The message to print.
     */
    public static void warn(String module, String message) {
        if (level > 1) return;
        System.out.println("\033[33mW [" + module + "]: " + message + "\033[0m");
    }

    /**
     * Log an error message to the console.
     * 
     * @param module The logging module.
     * @param message The message to print.
     */
    public static void error(String module, String message) {
        if (level > 2) return;
        System.err.println("\033[31mE [" + module + "]: " + message + "\033[0m");
    }

    /*
    public static void critical(String module, String message) {
        System.out.println("\033[32m[" + module + "]: " + message + "\033[0m");
    }*/
}
