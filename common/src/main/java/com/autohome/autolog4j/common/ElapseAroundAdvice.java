package com.autohome.autolog4j.common;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kcq on 2018/6/26.
 */
public class ElapseAroundAdvice implements MethodInterceptor {
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

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        if (isOpen) {
            String className = methodInvocation.getMethod().getDeclaringClass().getCanonicalName();
            String methodName = methodInvocation.getMethod().getName();
            long start = System.currentTimeMillis();
            Object returnValue = methodInvocation.proceed();
            long end = System.currentTimeMillis();
            write(className, methodName, (end - start));
            return returnValue;
        } else {
            return methodInvocation.proceed();
        }
    }

    private void write(String className, String methodName, long elapse) {
        String message = String.format("SlowClass=%s,SlowMethod=%s,Cost=%s", className, methodName, elapse);
        if (elapse >= this.minThresholdMillis && elapse >= this.thresholdMillis) {
            ELAPSE_TIMEOUT_LOG.warn(message);
        }
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
