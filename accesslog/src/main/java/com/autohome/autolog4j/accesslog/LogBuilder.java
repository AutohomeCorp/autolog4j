package com.autohome.autolog4j.accesslog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autohome.autolog4j.common.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Created by Menong on 2016/11/2.
 */
public class LogBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogBuilder.class);

    private final Map<String, String> items = Maps.newHashMapWithExpectedSize(20);
    private String separator = "\\t";
    private String placeholder = "-";
    private DateTime requestAt = DateTime.now();
    private Map<String, Object> responseJson;
    private ContentCachingRequestWrapper request;
    private ContentCachingResponseWrapper response;
    private Map<String, Integer> fieldLengthMap;
    private String host;


    public LogBuilder request(ContentCachingRequestWrapper request) {
        this.request = request;
        //addField(LogField.LOG_AT, this.requestAt.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        addField(LogField.CLIENT_IP, getClientIp());
        addField(LogField.HOST, findHost());
        addField(LogField.SERVER_IP, this.request.getLocalAddr());
        addField(LogField.HTTP_METHOD, this.request.getMethod());
        addField(LogField.URI_STEM, this.request.getRequestURI());
        addField(LogField.QUERY_STRING, this.request.getQueryString());
        addField(LogField.UA, this.request.getHeader("User-Agent"));
        addField(LogField.PLACEHOLDER, this.placeholder);
        addField(LogField.FORM_STRING, getRequestBody());
        addField(LogField.PARENT_TRACE_ID, this.request.getHeader("Parent-TraceId"));
        addField(LogField.TRACE_ID, getTraceId());
        addField(LogField.REFERER, this.request.getHeader("Referer"));
        addField(LogField.REMOTE_ADDR, this.request.getRemoteAddr());
        addField(LogField.X_FORWARDED_FOR, this.request.getHeader("x-forwarded-for"));
        addField(LogField.Q_TIME, System.currentTimeMillis() - this.requestAt.getMillis());

        return this;
    }

    public LogBuilder response(ContentCachingResponseWrapper response) {
        this.response = response;
        addField(LogField.STATUS, this.response.getStatusCode());
        addResponseContent();
        return this;
    }

    public LogBuilder host(String fixedHost) {
        this.host = fixedHost;
        return this;
    }

    public LogBuilder logAt(long millis) {
        this.requestAt = new DateTime(millis);
        return this;
    }

    public LogBuilder placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public LogBuilder separator(String separator) {
        this.separator = separator;
        return this;
    }

    public LogBuilder fieldLengthMap(Map<String, Integer> fieldLengthMap) {
        this.fieldLengthMap = fieldLengthMap;
        return this;
    }

    public LogBuilder addResponseContent() {
        return addField(LogField.RESPONSE_CONTENT, getResponseContent());
    }

    public LogBuilder addField(LogField field, Object value) {
        return addField(field.getName(), toString(value));
    }

    public LogBuilder addField(String field, String value) {
        this.items.put(field, value);
        return this;
    }

    public String getFieldValue(LogField field) {
        String val = this.items.get(field.getName());
        if (Strings.isNullOrEmpty(val)) {
            return "";
        }
        return val;
    }

    public LogBuilder toJson() {
        if (this.responseJson == null) {
            String responseBody = this.items.get(LogField.RESPONSE_CONTENT.getName());
            if (Strings.isNullOrEmpty(responseBody)) {
                return null;
            }
            try {
                this.responseJson = JacksonUtil.deserialize(responseBody, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ex) {
                this.responseJson = new HashMap<>();
                LOGGER.error("error when parse response", ex);
            }
        }
        return this;
    }

    private String toString(Object value) {
        if (value == null) {
            return null;
        }
        return (value instanceof String) ? (String) value : value.toString();
    }

    public String build(List<String> fields) {
        String[] fieldValueArray = new String[fields.size()];
        int qTimeIndex = 0;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equals(LogField.Q_TIME.getName())) {
                qTimeIndex = i;
            } else {
                fieldValueArray[i] = formatField(fields.get(i));
            }
        }
        fieldValueArray[qTimeIndex] = (System.currentTimeMillis() - this.requestAt.getMillis()) + "";

        return Joiner.on(this.separator).join(fieldValueArray);
        //return String.join(this.separator, fieldValueArray);
    }

    public String formatField(String input) {
        String[] array = input.split(":");
        String name = array[0];
        String value = this.items.get(name);
        if (Strings.isNullOrEmpty(value)) {
            return this.placeholder;
        }

        int length = array.length > 1 ? Integer.parseInt(array[1]) : LogField.getMaxLength(name);
        if (this.fieldLengthMap != null && this.fieldLengthMap.containsKey(name)) {
            length = this.fieldLengthMap.get(name);
        }
        if (length == 0) {
            return this.placeholder;
        }
        if (length > -1 && value.length() > length) {
            value = value.substring(0, length);
        }
        return value.replaceAll("[\r\n\t`]+", " ").replace(this.separator, "[separator]");
    }

    public String getClientIp() {
        String ip = this.request.getHeader("x-forwarded-for");
        return Strings.isNullOrEmpty(ip) ? this.request.getRemoteAddr() : ip;
    }

    public String getResponseValue(String name) {
        if (this.responseJson == null) {
            toJson();
        }
        if (this.responseJson == null) {
            return null;
        }
        if (this.responseJson.containsKey(name)) {
            return this.responseJson.get(name).toString();
        } else {
            return null;
        }
    }

    public String getParameter(String name) {
        return this.request.getParameter(name);
    }

    public String getRequestBody() {
        if (this.fieldLengthMap != null && this.fieldLengthMap.containsKey(LogField.FORM_STRING.getName())
                && this.fieldLengthMap.get(LogField.FORM_STRING.getName()) == 0) {
            return this.placeholder;
        }

        if (Strings.isNullOrEmpty(request.getContentType())
                || !this.request.getContentType().contains("json")) {
            return this.placeholder;
        }

        byte[] buffer = this.request.getContentAsByteArray();
        String payload = null;
        if (buffer.length > 0) {
            try {
                payload = new String(buffer, this.request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                payload = "[unknown]";
            }
        }
        return payload;
    }

    public String getResponseContent() {
        if (this.fieldLengthMap != null && this.fieldLengthMap.containsKey(LogField.RESPONSE_CONTENT.getName())
                && this.fieldLengthMap.get(LogField.RESPONSE_CONTENT.getName()) == 0) {
            return this.placeholder;
        }

        if (response.getContentType() != null && response.getContentType().contains("json")) {
            return new String(this.response.getContentAsByteArray(), Charsets.UTF_8);
        } else {
            return this.placeholder;
        }
    }

    public String getParentTraceId() {
        return this.request.getHeader("Parent-TraceId");
    }

    public String getTraceId() {
        Object traceObj = this.request.getAttribute("trace_id");
        return traceObj == null ? null : traceObj.toString();
    }

    public String findHost() {
        if (!Strings.isNullOrEmpty(host)) {
            return host;
        }
        return this.request.getServerName().toLowerCase();
    }

    public LogBuilder setRequestAtInMillis(Long millis) {
        this.requestAt = new DateTime(millis);
        addField(LogField.LOG_AT, this.requestAt.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

        return this;
    }
}