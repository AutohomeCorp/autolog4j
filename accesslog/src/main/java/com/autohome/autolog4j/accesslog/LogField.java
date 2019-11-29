package com.autohome.autolog4j.accesslog;

import java.util.Map;

import com.google.common.collect.Maps;


/**
 * Created by Menong on 2016/11/2.
 */
public class LogField {
    public static final Map<String, LogField> ALL_FIELD = Maps.newHashMap();
    public static final LogField LOG_AT = create("log_at", -1);
    public static final LogField Q_TIME = create("q_time", -1);
    public static final LogField CLIENT_IP = create("client_ip", -1);
    public static final LogField HOST = create("host", -1);
    public static final LogField SERVER_IP = create("server_ip", -1);
    public static final LogField HTTP_METHOD = create("http_method", -1);
    public static final LogField URI_STEM = create("uri_stem", -1);
    public static final LogField QUERY_STRING = create("query_string", -1);
    public static final LogField FORM_STRING = create("form_string", 800);
    public static final LogField UA = create("ua", -1);
    public static final LogField STATUS = create("status", -1);
    public static final LogField RESPONSE_CONTENT = create("response_content", 1000);
    public static final LogField CONSUMER_IP = create("consumer_ip", -1);
    public static final LogField ACCESS_TOKEN = create("access_token", -1);
    public static final LogField PLACEHOLDER = create("placeholder", -1);
    public static final LogField PARENT_TRACE_ID = create("parent_trace_id", -1);
    public static final LogField TRACE_ID = create("trace_id", -1);
    public static final LogField REFERER = create("referer", 1000);
    public static final LogField REMOTE_ADDR = create("remote_addr", -1);
    public static final LogField X_FORWARDED_FOR = create("x_forwarded_for", -1);

    private int maxLength;
    private String name;

    public static LogField create(String name, int maxLength) {
        LogField field = new LogField();
        field.setName(name);
        field.setMaxLength(maxLength);
        if (!ALL_FIELD.containsKey(name)) {
            ALL_FIELD.put(name, field);
        }
        return field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getMaxLength(String name) {
        LogField field = ALL_FIELD.get(name);
        return (field == null) ? -1 : field.getMaxLength();
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
