package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/14.
 */
public enum ExceptionCategory {
    UNKNOWN(0, "unknown"),
    REDIS(1, "redis"),
    MYSQL(2, "mysql"),
    SQLSERVER(3, "sqlserver"),
    KAFKA(4, "kafka"),
    RABBITMQ(5, "rabbitmq"),
    HBASE(6, "hbase"),
    SOLR(7, "solr"),
    ELASTICSEARCH(8, "elasticsearch"),
    HTTP(9, "http"),
    MONGODB(10, "mongodb"),
    METRICCOLLECT(11, "metriccollect");
    private int value;
    private String pattern;

    ExceptionCategory(int value, String pattern) {
        this.value = value;
        this.pattern = pattern;
    }

    public int getValue() {
        return value;
    }

    public String getPattern() {
        return pattern;
    }
}
