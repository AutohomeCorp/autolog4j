package com.autohome.autolog4j.metric.contract;

/**
 * Created by NFW on 2018/6/5.
 */
public class MetricBaseInfo {
    private String department;
    private String group;
    private String project;
    private String host;
    private String ip;

    public MetricBaseInfo() {
    }

    public MetricBaseInfo(String ip, String host) {
        this.ip = ip;
        this.host = host;
    }

    public MetricBaseInfo(String department, String group, String project, String host, String ip) {
        this.department = department;
        this.group = group;
        this.project = project;
        this.host = host;
        this.ip = ip;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
