package com.autohome.autolog4j.metric.impl;

import com.autohome.autolog4j.exception.MetricCollectException;
import com.autohome.autolog4j.exception.annotation.ExceptionWrapper;
import com.autohome.autolog4j.metric.IGraphiteClient;
import com.autohome.autolog4j.metric.contract.MetricBaseInfo;
import com.autohome.autolog4j.metric.enums.MetricTypeEnum;
import com.autohome.autolog4j.metric.utils.MetricUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by NFW on 2018/5/8.
 */
@Deprecated
@ExceptionWrapper(toType = MetricCollectException.class)
public class LogGraphiteClient implements IGraphiteClient {
    private final Logger client;
    private String prefix = null;

    private LogGraphiteClient(String loggerName) {
        MetricUtil.validateNotNull(loggerName, "loggerName");
        this.client = LoggerFactory.getLogger(loggerName);
    }

    public LogGraphiteClient(String loggerName, MetricBaseInfo base) {
        this(loggerName);
        MetricUtil.validateNotNull(base, "MetricBaseInfo");
        this.prefix = MetricUtil.formatPrefix(base, MetricTypeEnum.METRICTYPE_USER);
    }

    public LogGraphiteClient(String loggerName, MetricBaseInfo base, MetricTypeEnum metricType) {
        this(loggerName);
        MetricUtil.validateNotNull(base, "BaseInfo");
        MetricUtil.validateNotNull(metricType, "metricType");
        this.prefix = MetricUtil.formatPrefix(base, metricType);
    }

    public void sendMetric(String name, String value, long timestamp) {
        send(MetricUtil.messageForGraphite(name, value, timestamp, this.prefix));
    }

    @Override
    public void sendMetric(String name, double value, long timestamp) {
        sendMetric(name, String.valueOf(value), timestamp);
    }

    @Override
    public void sendMetric(String name, long value, long timestamp) {
        sendMetric(name, String.valueOf(value), timestamp);
    }

    @Override
    public void sendMetric(String name, int value, long timestamp) {
        sendMetric(name, String.valueOf(value), timestamp);
    }

    @Override
    public void sendMetric(String name, float value, long timestamp) {
        sendMetric(name, String.valueOf(value), timestamp);
    }

    @Override
    public void sendMetric(String name, short value, long timestamp) {
        sendMetric(name, String.valueOf(value), timestamp);
    }

    public void sendMetric(String name, String value) {
        sendMetric(name, String.valueOf(value), System.currentTimeMillis() / 1000);
    }

    @Override
    public void sendMetric(String name, double value) {
        sendMetric(name, String.valueOf(value));
    }

    @Override
    public void sendMetric(String name, long value) {
        sendMetric(name, String.valueOf(value));
    }

    @Override
    public void sendMetric(String name, int value) {
        sendMetric(name, String.valueOf(value));
    }

    @Override
    public void sendMetric(String name, float value) {
        sendMetric(name, String.valueOf(value));
    }

    @Override
    public void sendMetric(String name, short value) {
        sendMetric(name, String.valueOf(value));
    }

    private void send(String message) {
        try {
            client.trace(message);
        } catch (Exception e) {
            throw new MetricCollectException("Error when sending metrics ", e);
        }
    }
}
