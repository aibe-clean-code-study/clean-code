package util.logger;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CustomLogger {
    private final Logger logger;

    public CustomLogger(Class<?> clazz) {
        this.logger = Logger.getLogger(clazz.getName());
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        setLoggerLevel(dotenv);
    }

    private void setLoggerLevel(Dotenv dotenv) {
        String mode = Optional.of(dotenv.get("MODE"))
                .orElseThrow(() -> new RuntimeException("mode is missing"));

        switch (mode) {
            case "DEV" -> this.logger.setLevel(Level.INFO);
            case "PROD" -> this.logger.setLevel(Level.SEVERE);
        }
    }

    public void info(String msg) {
        LogRecord record = new LogRecord(Level.INFO, msg);
        record.setSourceClassName(logger.getName());
        logger.log(record);
    }

    public void severe(String msg) {
        LogRecord record = new LogRecord(Level.SEVERE, msg);
        record.setSourceClassName(logger.getName());
        logger.log(record);
    }

}
