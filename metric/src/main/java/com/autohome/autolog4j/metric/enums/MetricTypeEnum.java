package com.autohome.autolog4j.metric.enums;

/**
 * Created by NFW on 2018/6/5.
 */
public enum MetricTypeEnum {
    METRICTYPE_SYS(1, "sys"),//系统数据
    METRICTYPE_USER(2, "user");//用户数据/业务数据
    private int code;
    private String name;

    MetricTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
