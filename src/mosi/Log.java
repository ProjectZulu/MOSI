package mosi;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final String LOG_NAME = "MOSI";
    private static Logger myLog;
    private static Log log;
    private static boolean isSetup;

    public static Log log() {
        return log;
    }

    private class LogType {
        private final boolean isEnabled;

        public LogType(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public boolean isEnabled() {
            return isEnabled;
        }
    }

    public final String FILE_VERSION = "1.0";
    private final LogType DEBUG;

    public Log() {
        DEBUG = new LogType(false);
        log = this;
    }

    public static void setLogger(Log log) {
        if (!isSetup) {
            Log.isSetup = true;
            Log.log = log;
            Log.myLog = Logger.getLogger(LOG_NAME);
            Log.myLog.setParent(Logger.getLogger("ForgeModLoader"));
        }
    }

    public void log(Level level, String format, Object... data) {
        myLog.log(level, String.format(format, data));
    }

    public void info(String format, Object... data) {
        log(Level.INFO, format, data);
    }

    public void warning(String format, Object... data) {
        log(Level.WARNING, format, data);
    }

    public void severe(String format, Object... data) {
        log(Level.SEVERE, format, data);
    }

    public void debug(Level level, String format, Object... data) {
        if (DEBUG.isEnabled()) {
            log(level, format, data);
        }
    }

    public void log(LogType type, Level level, String format, Object... data) {
        if (type.isEnabled()) {
            log(level, format, data);
        }
    }
}
