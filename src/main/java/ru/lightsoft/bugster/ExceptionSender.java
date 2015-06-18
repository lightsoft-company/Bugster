package ru.lightsoft.bugster;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by lav on 11.06.2015.
 */
public class ExceptionSender {
    public static final Logger logger = LoggerFactory.getLogger(ExceptionSender.class);
    private static HashMap<String, String> httpHeader = newHashMap();

    static {
        httpHeader.put("Content-Type", "application/json");
    }

    private Exception lastError;

    public void event(ILoggingEvent event, BugsterConfig config) {
        if (config.isEnabled() && isError(event)) {
            Message msg = new Message(event, config);

            boolean success = sendException(msg, config);
            if (!success) {
                logger.info("Failed to send log message through API: " + lastError);
            }
        }
    }

    private boolean sendException(Message msg, BugsterConfig config) {
        try {
            String URL = String.format("http://%s/report", config.getHost());
            return Utils.httpPost(URL, Utils.print(msg), httpHeader);
        } catch (Exception err) {
            lastError = err;
            return false;
        }
    }

    private boolean isError(ILoggingEvent event) {
        return event.getLevel().equals(Level.ERROR) || event.getLevel().equals(Level.WARN);
    }
}
