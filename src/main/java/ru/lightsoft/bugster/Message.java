package ru.lightsoft.bugster;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;

/**
 * Сообщение с ошибкой и доп. данными для отправки в Bugster
 */
public class Message {
    private String environmentName;
    private String problemText;
    private String description;
    private String extraInformation;
    private int projectId;
    private String secretCode;

    public Message(ILoggingEvent event, BugsterConfig config) {
        Map<String, Object> extraInfo = Utils.collectExtraInfo(event);

        environmentName = config.getEnvironment();
        problemText = generateMsgTitle(event);
        description = Utils.formatStackTrace(event);
        projectId = config.getProjectId();
        secretCode = generateSecretCode(config.getProjectKey());
        extraInformation = Utils.print(extraInfo);
    }

    private String generateMsgTitle(ILoggingEvent event) {
        String msg = event.getMessage();
        IThrowableProxy throwableProxy = event.getThrowableProxy();

        if (msg.isEmpty() && throwableProxy != null && throwableProxy.getMessage() != null) {
            msg = throwableProxy.getMessage();
        }

        if (throwableProxy != null) {
            StackTraceElementProxy[] traces = throwableProxy.getStackTraceElementProxyArray();
            if (msg.isEmpty() && traces.length > 0) {
                msg = traces[0].getStackTraceElement().toString();
            }

            msg = "[" + throwableProxy.getClassName() + "] " + msg;
        }

        return msg;
    }

    private String generateSecretCode(String projectKey) {
        return DigestUtils.md5Hex(problemText + projectKey);
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getProblemText() {
        return problemText;
    }

    public String getDescription() {
        return description;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getSecretCode() {
        return secretCode;
    }
}