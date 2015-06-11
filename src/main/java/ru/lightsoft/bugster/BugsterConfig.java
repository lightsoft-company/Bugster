package ru.lightsoft.bugster;

/**
 * Конфиг багстера
 */
public class BugsterConfig {
    private String host;
    private int projectId;
    private String projectKey;
    private String environment;
    private boolean enabled;

    public BugsterConfig() {
    }

    public BugsterConfig(String host, int projectId, String projectKey, String environment, boolean enabled) {
        this.host = host;
        this.projectId = projectId;
        this.projectKey = projectKey;
        this.environment = environment;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}