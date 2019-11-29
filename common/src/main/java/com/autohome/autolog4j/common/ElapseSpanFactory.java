package com.autohome.autolog4j.common;

/**
 * Created by kcq on 2018/9/4.
 */
public class ElapseSpanFactory {

    private int thresholdMillis = 500;

    private int minThresholdMillis = 100;

    public int getThresholdMillis() {
        return thresholdMillis;
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

    public ElapseSpan createSpan(Class<?> clazz, String spanName) {
        return new ElapseSpan(clazz, spanName, this.thresholdMillis, this.minThresholdMillis);
    }
}
