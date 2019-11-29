package com.autohome.autolog4j.metric.impl;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.autohome.autolog4j.exception.MetricCollectException;
import com.autohome.autolog4j.exception.annotation.ExceptionWrapper;
import com.autohome.autolog4j.metric.IInfluxClient;
import com.autohome.autolog4j.metric.contract.InfluxPoint;
import com.autohome.autolog4j.metric.contract.MetricBaseInfo;
import com.autohome.autolog4j.metric.utils.MetricUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by NFW on 2018/8/2.
 */
@ExceptionWrapper(toType = MetricCollectException.class)
public class LogInfluxClient implements IInfluxClient {
    private Consumer<String> sendSink;
    private Map<String, String> commonTags = new TreeMap<>();

    private LogInfluxClient() {
    }

    public static LogInfluxClient.Builder client(Consumer<String> sendSink) {
        return new LogInfluxClient.Builder(sendSink);
    }

    @Override
    public void write(InfluxPoint point) {
        write(MetricUtil.messageForInflux(point, commonTags));
    }

    @Override
    public void write(String line) {
        try {
            sendSink.accept(line);
        } catch (Exception e) {
            throw new MetricCollectException("Error when sending metrics ", e);
        }
    }

    public void setSendSink(Consumer<String> sendSink) {
        this.sendSink = sendSink;
    }

    public Map<String, String> getCommonTags() {
        return commonTags;
    }

    public void setCommonTags(Map<String, String> commonTags) {
        this.commonTags = commonTags;
    }

    public static final class Builder {
        private final Consumer<String> sendSink;
        private final Map<String, String> commonTags = new TreeMap<>();

        /**
         * @param sendSink the sendSink to set
         */
        Builder(Consumer<String> sendSink) {
            this.sendSink = sendSink;
        }

        /**
         * Add common tag to this client.
         *
         * @param metricBaseInfo the client baseInfo name
         * @return the Builder instance.
         */
        public Builder commonTag(MetricBaseInfo metricBaseInfo) {
            MetricUtil.validateNotNull(metricBaseInfo, "metricBaseInfo");
            if (!StringUtils.isEmpty(metricBaseInfo.getDepartment())) {
                commonTags.put("department", metricBaseInfo.getDepartment());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getGroup())) {
                commonTags.put("group", metricBaseInfo.getGroup());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getHost())) {
                commonTags.put("host", metricBaseInfo.getHost());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getIp())) {
                commonTags.put("ip", metricBaseInfo.getIp());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getProject())) {
                commonTags.put("project", metricBaseInfo.getProject());
            }
            return this;
        }

        /**
         * Add commonTags to this client.
         *
         * @param tagsToAdd the Map of tags to add
         * @return the Builder instance.
         */
        public Builder commonTag(Map<String, String> tagsToAdd) {
            MetricUtil.validateNotNull(tagsToAdd, "tagsToAdd");
            for (Map.Entry<String, String> tag : tagsToAdd.entrySet()) {
                commonTag(tag.getKey(), tag.getValue());
            }
            return this;
        }

        /**
         * Add commonTags to this client.
         *
         * @param tagName the tag name
         * @param value   the tag value
         * @return the Builder instance.
         */
        public Builder commonTag(String tagName, String value) {
            MetricUtil.validateNotNull(tagName, "tagName");
            MetricUtil.validateNotNull(value, "value");
            commonTags.put(tagName, value);
            return this;
        }

        /**
         * Create a new LogStatsdClient.
         *
         * @return the new created LogStatsdClient.
         */
        public LogInfluxClient build() {
            LogInfluxClient logStatsdClient = new LogInfluxClient();
            logStatsdClient.setSendSink(sendSink);
            logStatsdClient.setCommonTags(commonTags);
            return logStatsdClient;
        }
    }
}
