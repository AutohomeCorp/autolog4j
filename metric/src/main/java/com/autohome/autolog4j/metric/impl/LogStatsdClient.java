package com.autohome.autolog4j.metric.impl;

import java.util.Map;
import java.util.TreeMap;

import com.autohome.autolog4j.exception.MetricCollectException;
import com.autohome.autolog4j.exception.annotation.ExceptionWrapper;
import com.autohome.autolog4j.metric.IStatsdClient;
import com.autohome.autolog4j.metric.contract.MetricBaseInfo;
import com.autohome.autolog4j.metric.contract.StatsdPoint;
import com.autohome.autolog4j.metric.utils.MetricUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by NFW on 2018/5/7.
 */
@ExceptionWrapper(toType = MetricCollectException.class)
public class LogStatsdClient implements IStatsdClient {
    private Logger client;
    private double sampleRate = 1.0;
    private Map<String, String> commonTags = new TreeMap<>();

    private LogStatsdClient() {
    }

    public static Builder client(String loggerName) {
        return new Builder(loggerName);
    }

    @Override
    public void count(StatsdPoint point, int value) {
        count(point, value, sampleRate);
    }

    @Override
    public void count(StatsdPoint point, long value) {
        count(point, value, sampleRate);
    }

    @Override
    public void count(StatsdPoint point, long value, double sampleRate) {
        countCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void count(StatsdPoint point, int value, double sampleRate) {
        countCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void incrementCounter(StatsdPoint point) {
        count(point, 1, 1.0);
    }

    @Override
    public void decrementCounter(StatsdPoint point) {
        count(point, -1, 1.0);
    }

    @Override
    public void recordGaugeDelta(StatsdPoint point, long delta) {
        recordGaugeCommon(point, String.valueOf(delta), delta < 0, true);
    }

    @Override
    public void recordGaugeDelta(StatsdPoint point, double delta) {
        recordGaugeCommon(point, String.valueOf(delta), delta < 0, true);
    }

    @Override
    public void recordGaugeDelta(StatsdPoint point, int delta) {
        recordGaugeCommon(point, String.valueOf(delta), delta < 0, true);
    }

    @Override
    public void recordGaugeDelta(StatsdPoint point, float delta) {
        recordGaugeCommon(point, String.valueOf(delta), delta < 0, true);
    }

    @Override
    public void recordGaugeDelta(StatsdPoint point, short delta) {
        recordGaugeCommon(point, String.valueOf(delta), delta < 0, true);
    }

    @Override
    public void recordGaugeValue(StatsdPoint point, long value) {
        recordGaugeCommon(point, String.valueOf(value), value < 0, false);
    }

    @Override
    public void recordGaugeValue(StatsdPoint point, double value) {
        recordGaugeCommon(point, String.valueOf(value), value < 0, false);
    }

    @Override
    public void recordGaugeValue(StatsdPoint point, int value) {
        recordGaugeCommon(point, String.valueOf(value), value < 0, false);
    }

    @Override
    public void recordGaugeValue(StatsdPoint point, float value) {
        recordGaugeCommon(point, String.valueOf(value), value < 0, false);
    }

    @Override
    public void recordGaugeValue(StatsdPoint point, short value) {
        recordGaugeCommon(point, String.valueOf(value), value < 0, false);
    }

    @Override
    public void recordSetEvent(StatsdPoint point, String value) {
        send(MetricUtil.messageForStatsd(point, value, "s", commonTags));
    }

    @Override
    public void recordTiming(StatsdPoint point, long value) {
        recordTiming(point, value, sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, long value, double sampleRate) {
        recordTimingCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, double value) {
        recordTiming(point, value, sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, int value) {
        recordTiming(point, value, sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, float value) {
        recordTiming(point, value, sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, short value) {
        recordTiming(point, value, sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, double value, double sampleRate) {
        recordTimingCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, int value, double sampleRate) {
        recordTimingCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, float value, double sampleRate) {
        recordTimingCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void recordTiming(StatsdPoint point, short value, double sampleRate) {
        recordTimingCommon(point, String.valueOf(value), sampleRate);
    }

    @Override
    public void send(String message) {
        try {
            client.trace(message);
        } catch (Exception e) {
            throw new MetricCollectException("Error when sending metrics ", e);
        }
    }

    @Override
    public void send(String message, double sampleRate) {
        MetricUtil.validateSampleRate(sampleRate);
        if (checkMessageNotWorkForSampleRate(message)) {
            send(message);
        } else if (sampleRate == 1.0 || MetricUtil.needSend(sampleRate)) {
            send(sampleRate == 1.0 ? message : String.format("%s|@%s", message, sampleRate));
        }
    }

    private boolean checkMessageNotWorkForSampleRate(String message) {
        if (message.endsWith("|g") || message.endsWith("|s")) {
            return true;
        }
        return false;
    }

    private void countCommon(StatsdPoint point, String value, double sampleRate) {
        MetricUtil.validateSampleRate(sampleRate);
        if (sampleRate == 1.0 || MetricUtil.needSend(sampleRate)) {
            send(MetricUtil.messageForStatsd(point, value, "c", sampleRate, commonTags));
        }
    }

    private void recordTimingCommon(StatsdPoint point, String value, double sampleRate) {
        MetricUtil.validateSampleRate(sampleRate);
        if (sampleRate == 1.0 || MetricUtil.needSend(sampleRate)) {
            send(MetricUtil.messageForStatsd(point, String.valueOf(value), "ms", sampleRate, commonTags));
        }
    }

    private void recordGaugeCommon(StatsdPoint point, String value, boolean negative, boolean delta) {
        StringBuilder message = new StringBuilder();
        if (!delta && negative) {
            message.append(MetricUtil.messageForStatsd(point, "0", "g", commonTags)).append("\n ");
        }
        message.append(MetricUtil.messageForStatsd(point, String.format("%s%s", (delta && !negative) ? "+" : "", value),
                "g", commonTags));
        send(message.toString());
    }

    public void setClient(Logger client) {
        this.client = client;
    }

    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Map<String, String> getCommonTags() {
        return commonTags;
    }

    public void setCommonTags(Map<String, String> commonTags) {
        this.commonTags = commonTags;
    }

    public static final class Builder {
        private final Logger client;
        private final Map<String, String> commonTags = new TreeMap<>();
        private double sampleRate = 1.0;

        /**
         * @param loggerName the loggerName to set
         */
        Builder(String loggerName) {
            MetricUtil.validateNotNull(loggerName, "loggerName");
            client = LoggerFactory.getLogger(loggerName);
        }

        /**
         * Add a sampleRate to this client.
         *
         * @param sampleRate the sampleRate value
         * @return the Builder instance.
         */
        public Builder sampleRate(double sampleRate) {
            MetricUtil.validateSampleRate(sampleRate);
            this.sampleRate = sampleRate;
            return this;
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
            if (!StringUtils.isEmpty(metricBaseInfo.getProject())) {
                commonTags.put("project", metricBaseInfo.getProject());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getHost())) {
                commonTags.put("host", metricBaseInfo.getHost());
            }
            if (!StringUtils.isEmpty(metricBaseInfo.getIp())) {
                commonTags.put("ip", metricBaseInfo.getIp());
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
         * Create a new LogStatsdClient.
         *
         * @return the new created LogStatsdClient.
         */
        public LogStatsdClient build() {
            LogStatsdClient logStatsdClient = new LogStatsdClient();
            logStatsdClient.setClient(client);
            logStatsdClient.setCommonTags(commonTags);
            logStatsdClient.setSampleRate(sampleRate);
            return logStatsdClient;
        }
    }
}
