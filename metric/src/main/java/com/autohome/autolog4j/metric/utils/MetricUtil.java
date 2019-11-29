package com.autohome.autolog4j.metric.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.autohome.autolog4j.metric.contract.InfluxPoint;
import com.autohome.autolog4j.metric.contract.MetricBaseInfo;
import com.autohome.autolog4j.metric.contract.StatsdPoint;
import com.autohome.autolog4j.metric.enums.MetricTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by NFW on 2018/6/13.
 */
public class MetricUtil {
    public static String findServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public static String sanitize(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (value.endsWith("\\")) {
            value = value.substring(0, value.length() - 1).concat("_");
        }
        return value.trim().replace(" ", "_").replace(",", "_").replace("=", "_").replace(":", "_").replace("|", "_");
    }

    public static String sanitizeField(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('\"');
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '\\':
                case '\"':
                    sb.append('\\');
                    break;
                default:
            }
            sb.append(value.charAt(i));
        }
        sb.append('\"');
        return sb.toString();
    }

    @Deprecated
    public static void escapeKeys(StringBuffer sb, String key) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        sb.append(sanitize(key));
    }

    public static void validateSampleRate(Double sampleRate) {
        if (sampleRate == null || sampleRate < 0 || sampleRate > 1) {
            throw new IllegalArgumentException("sampleRate must be between 0 and 1");
        }
    }

    public static void validateNotNull(String value, String name) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(String.format("%s %s", name, "must not be null or \"\" "));
        }
    }

    public static void validateNotNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("%s %s", name, "must not be null"));
        }
    }

    public static void validateNotEmpty(Map value, String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s %s", name, "must not be empty"));
        }
    }

    public static String messageForStatsd(StatsdPoint point, String value, String type, double sampleRate, Map<String, String> commonTags) {
        String message = messageForStatsd(point, value, type, commonTags);
        return (sampleRate == 1.0)
                ? message
                : String.format("%s|@%s", message, sampleRate);
    }

    public static String messageForStatsd(StatsdPoint point, String value, String type, Map<String, String> commonTags) {
        return String.format("%s%s:%s|%s", MetricUtil.sanitize(point.getMeasurement()),
                concatenatedTags(commonTags, point.getTags()), value, type);
    }

    public static String messageForGraphite(String name, String value, long timestamp, String prefix) {
        return String.format("%s%s %s %s", prefix == null ? "" : prefix, name, value, timestamp);
    }

    public static String messageForInflux(InfluxPoint point, Map<String, String> commonTags) {
        return String.format("%s%s %s %s", point.getMeasurement(), concatenatedTags(commonTags, point.getTags()), concatenatedFields(point
                .getFields()), concatenatedTimestamp(TimeUnit.NANOSECONDS, point.getTime(), point.getPrecision()));
    }

    public static Boolean needSend(double sampleRate) {
        return ThreadLocalRandom.current().nextDouble() <= sampleRate;
    }

    public static String formatPrefix(MetricBaseInfo base, MetricTypeEnum metricType) {
        return String.format("%s.%s.%s.%s.%s.%s.", metricType.getName(), MetricUtil.sanitize(base.getDepartment()),
                MetricUtil.sanitize(base.getGroup()),
                MetricUtil.sanitize(base.getProject()),
                MetricUtil.sanitize(base.getHost()),
                MetricUtil.sanitize(base.getIp()));
    }

    public static String concatenatedTags(Map<String, String> commonTags, Map<String, String> customTags) {
        StringBuffer sb = new StringBuffer();
        Map<String, String> tags = new TreeMap<>();
        if (!(commonTags == null || commonTags.isEmpty())) {
            tags.putAll(commonTags);
        }
        if (!(customTags == null || customTags.isEmpty())) {
            tags.putAll(customTags);
        }
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            sb.append(',');
            sb.append(MetricUtil.sanitize(tag.getKey()));
            sb.append('=');
            sb.append(MetricUtil.sanitize(tag.getValue()));
        }
        return sb.toString();
    }

    public static String concatenatedFields(Map<String, Object> fields) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            Object value = field.getValue();
            if (value == null) {
                continue;
            }
            sb.append(MetricUtil.sanitize(field.getKey()));
            sb.append('=');
            sb.append(value instanceof String ? MetricUtil.sanitizeField(String.valueOf(value)) : value);
            sb.append(',');
        }
        int lengthMinusOne = sb.length() - 1;
        if (sb.charAt(lengthMinusOne) == ',') {
            sb.setLength(lengthMinusOne);
        }
        return sb.toString();
    }

    public static String concatenatedTimestamp(TimeUnit targetTimeUnit, Long sourceTime, TimeUnit sourceTimeUnit) {
        if (sourceTime == null || sourceTimeUnit == null) {
            return "";
        }
        return String.valueOf(TimeUnit.NANOSECONDS.convert(sourceTime, sourceTimeUnit));
    }
}
