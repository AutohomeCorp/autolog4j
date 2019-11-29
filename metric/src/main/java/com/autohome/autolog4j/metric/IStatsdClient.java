package com.autohome.autolog4j.metric;


import com.autohome.autolog4j.metric.contract.StatsdPoint;

/**
 * Created by NFW on 2018/5/7.
 */
public interface IStatsdClient {
    /**
     * Adjusts the specified counter by a given value.
     *
     * @param point the metric of the counter to adjust
     * @param value the amount to adjust the counter by
     */
    void count(StatsdPoint point, int value);

    /**
     * Convenience method equivalent to {@link #count(StatsdPoint, int)} but for long deltas.
     */
    void count(StatsdPoint point, long value);

    /**
     * Adjusts the specified counter by a given value.
     *
     * @param point      the metric of the counter to adjust
     * @param value      the amount to adjust the counter by
     * @param sampleRate the sampling rate being employed. For example, a rate of 0.1 would tell StatsD that this counter is being sent
     *                   sampled every 1/10th of the time.
     */
    void count(StatsdPoint point, int value, double sampleRate);

    /**
     * Convenience method equivalent to {@link #count(StatsdPoint, int, double)} but for long deltas.
     */
    void count(StatsdPoint point, long value, double sampleRate);

    /**
     * Increments the specified counter by one.
     *
     * @param point the metric of the counter to increment
     */
    void incrementCounter(StatsdPoint point);

    /**
     * Decrements the specified counter by one.
     *
     * @param point the metric of the counter to decrement
     */
    void decrementCounter(StatsdPoint point);

    /**
     * Records a change in the value of the specified named gauge.
     *
     * @param point the metric of the gauge
     * @param delta the +/- delta to apply to the gauge
     */
    void recordGaugeDelta(StatsdPoint point, long delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeDelta(StatsdPoint, long)} but for double deltas.
     */
    void recordGaugeDelta(StatsdPoint point, double delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeDelta(StatsdPoint, long)} but for int deltas.
     */
    void recordGaugeDelta(StatsdPoint point, int delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeDelta(StatsdPoint, long)} but for float deltas.
     */
    void recordGaugeDelta(StatsdPoint point, float delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeDelta(StatsdPoint, long)} but for short deltas.
     */
    void recordGaugeDelta(StatsdPoint point, short delta);

    /**
     * Records the value of the specified named gauge.
     *
     * @param point the metric of the gauge
     * @param value the value to apply to the gauge
     */
    void recordGaugeValue(StatsdPoint point, long value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(StatsdPoint, long)} but for double deltas.
     */
    void recordGaugeValue(StatsdPoint point, double value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(StatsdPoint, long)} but for int deltas.
     */
    void recordGaugeValue(StatsdPoint point, int value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(StatsdPoint, long)} but for float deltas.
     */
    void recordGaugeValue(StatsdPoint point, float value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(StatsdPoint, long)} but for short deltas.
     */
    void recordGaugeValue(StatsdPoint point, short value);

    /**
     * StatsD supports counting unique occurrences of events between flushes, Call this method to records an occurrence
     * of the specified named event.
     *
     * @param point the metric of the set
     * @param value the value to be added to the set
     */
    void recordSetEvent(StatsdPoint point, String value);

    /**
     * Records StatsD figures out percentiles, average (mean), standard deviation, sum, lower and upper bounds for the specified named
     * event
     *
     * @param point the metric of the timed operation
     * @param value the value to be added to the timing
     */
    void recordTiming(StatsdPoint point, long value);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long)} but for double value.
     */
    void recordTiming(StatsdPoint point, double value);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long)} but for int value.
     */
    void recordTiming(StatsdPoint point, int value);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long)} but for float value.
     */
    void recordTiming(StatsdPoint point, float value);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long)} but for short value.
     */
    void recordTiming(StatsdPoint point, short value);

    /**
     * Records StatsD figures out percentiles, average (mean), standard deviation, sum, lower and upper bounds for the specified named
     * event.
     *
     * @param point      the metric of the timed operation
     * @param value      the value to be added to the timing
     * @param sampleRate the sampling rate being employed. For example, a rate of 0.1 would tell StatsD that this timer is being sent
     *                   sampled every 1/10th of the time.
     */
    void recordTiming(StatsdPoint point, long value, double sampleRate);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long, double)} but for double value.
     */
    void recordTiming(StatsdPoint point, double value, double sampleRate);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long, double)} but for int value.
     */
    void recordTiming(StatsdPoint point, int value, double sampleRate);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long, double)} but for float value.
     */
    void recordTiming(StatsdPoint point, float value, double sampleRate);

    /**
     * Convenience method equivalent to {@link #recordTiming(StatsdPoint, long, double)} but for short value.
     */
    void recordTiming(StatsdPoint point, short value, double sampleRate);

    /**
     * Records StatsD figures out percentiles, average (mean), standard deviation, sum, lower and upper bounds for the specified named
     * event.
     *
     * @param line       the value to be sended
     * @param sampleRate the sampling rate being employed. For example, a rate of 0.1 would tell StatsD that this timer is being sent
     *                   sampled every 1/10th of the time.(only for Timing & Guage)
     */
    void send(String line, double sampleRate);

    /**
     * Convenience method equivalent to {@link #send(String, double)} but for default sampleRate 1.
     */
    void send(String line);
}
