package com.autohome.autolog4j.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxiaoming on 2015/8/31.
 */
public class ElapseInterceptor {
    private static Logger ELAPSE_TIMEOUT_LOG = LoggerFactory.getLogger("elapseTimeOutLogger");

    /**
     * 是否打开超时日志
     */
    private Boolean isOpen = Boolean.TRUE;

    private int thresholdMillis = 500;

    private int minThresholdMillis = 100;

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (isOpen) {
            String className = pjp.getSignature().getDeclaringTypeName();
            String methodName = pjp.getSignature().getName();
            long start = System.currentTimeMillis();
            Object returnValue = pjp.proceed();
            long end = System.currentTimeMillis();
            write(className, methodName, (end - start));
            return returnValue;
        } else {
            return pjp.proceed();
        }
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

    public int getThresholdMillis() {
        return this.thresholdMillis;
    }

    public void setThresholdMillis(int thresholdMillis) {
        this.thresholdMillis = thresholdMillis;
    }

    public int getMinThresholdMillis() {
        return minThresholdMillis;
    }

    public void setMinThresholdMillis(int minThresholdMillis) {
        this.minThresholdMillis = minThresholdMillis;
    }
}