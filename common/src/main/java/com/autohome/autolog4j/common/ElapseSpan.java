package com.autohome.autolog4j.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kcq on 2018/9/4.
 */
public class ElapseSpan {
    private static Logger ELAPSE_TIMEOUT_LOG = LoggerFactory.getLogger("elapseTimeOutLogger");

    private final Class<?> clazz;

    private final String spanName;

    private final int thresholdMillis;

    private final int minThresholdMillis;

    private final long start;

    ElapseSpan(Class<?> clazz, String spanName, int thresholdMillis, int minThresholdMillis) {
        this.clazz = clazz;
        this.spanName = spanName;
        this.thresholdMillis = thresholdMillis;
        this.minThresholdMillis = minThresholdMillis;
        start = System.currentTimeMillis();
    }

    public void close() {
        long end = System.currentTimeMillis();
        long elapse = end - start;

        write(this.clazz.getCanonicalName(), this.spanName, elapse);
    }

    private String generatePerformanceMesssage(String className, String methodName, long elapse) {
        return String.format("SlowClass=%s,SlowMethod=%s,Cost=%s", className, methodName, elapse);
    }

    private void writeLog(String message, long elapse) {
        if (elapse >= this.minThresholdMillis && elapse >= this.thresholdMillis) {
            ELAPSE_TIMEOUT_LOG.warn(message);
        }
    }

    private void write(String className, String methodName, long elapse) {
        String message = generatePerformanceMesssage(className, methodName, elapse);
        writeLog(message, elapse);
    }
}
