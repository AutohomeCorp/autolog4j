package com.autohome.autolog4j.metric;

/**
 * Created by NFW on 2018/5/8.
 */
@Deprecated
public interface IGraphiteClient {
    /**
     * Sends the given measurement to the server.
     *
     * @param name      the name of the metric
     * @param value     the value of the metric
     * @param timestamp the timestamp of the metric , unit: second
     */
    void sendMetric(String name, double value, long timestamp);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double, long)} but for long value.
     */
    void sendMetric(String name, long value, long timestamp);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double, long)} but for int value.
     */
    void sendMetric(String name, int value, long timestamp);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double, long)} but for float value.
     */
    void sendMetric(String name, float value, long timestamp);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double, long)} but for short value.
     */
    void sendMetric(String name, short value, long timestamp);

    /**
     * Sends the given measurement to the server.
     * Timestamp  Default current time
     *
     * @param name  the name of the metric
     * @param value the value of the metric
     */
    void sendMetric(String name, double value);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double)} but for long value.
     */
    void sendMetric(String name, long value);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double)} but for int value.
     */
    void sendMetric(String name, int value);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double)} but for float value.
     */
    void sendMetric(String name, float value);

    /**
     * Convenience method equivalent to {@link #sendMetric(String, double)} but for short value.
     */
    void sendMetric(String name, short value);

}
