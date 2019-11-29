package com.autohome.autolog4j.metric.contract;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.autohome.autolog4j.metric.utils.MetricUtil;


/**
 * Created by NFW on 2018/7/5.
 */
public class InfluxPoint {
    private String measurement;
    private Map<String, String> tags;
    private Map<String, Object> fields;
    private Long time;
    private TimeUnit precision = TimeUnit.NANOSECONDS;

    private InfluxPoint() {
    }

    public static Builder measurement(final String measurement) {
        return new Builder(measurement);
    }

    /**
     * @return the measurement
     */
    public String getMeasurement() {
        return measurement;
    }

    /**
     * @param measurement the measurement to set
     */
    void setMeasurement(final String measurement) {
        this.measurement = measurement;
    }

    /**
     * @return the tags
     */
    public Map<String, String> getTags() {
        return this.tags;
    }

    /**
     * @param tags the tags to set
     */
    void setTags(final Map<String, String> tags) {
        this.tags = tags;
    }

    /**
     * @return the fields
     */
    public Map<String, Object> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    /**
     * @return the time
     */
    public Long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * @return the precision
     */
    public TimeUnit getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(TimeUnit precision) {
        this.precision = precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(measurement, tags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Point [name=");
        builder.append(this.measurement);
        builder.append(", tags=");
        builder.append(this.tags);
        builder.append(", fields=");
        builder.append(this.fields);
        builder.append(", time=");
        builder.append(this.time);
        builder.append(", precision=");
        builder.append(this.precision);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Builder for a new Point.
     */
    public static final class Builder {
        private final String measurement;
        private final Map<String, String> tags = new TreeMap<>();
        private final Map<String, Object> fields = new TreeMap<>();
        private Long time;
        private TimeUnit precision = TimeUnit.NANOSECONDS;

        /**
         * @param measurement the measurement to set
         */
        Builder(final String measurement) {
            this.measurement = measurement;
        }

        /**
         * Add a tag to this point.
         *
         * @param tagName the tag name
         * @param value   the tag value
         * @return the Builder instance.
         */
        public Builder tag(final String tagName, final String value) {
            MetricUtil.validateNotNull(tagName, "tagName");
            MetricUtil.validateNotNull(value, "value");
            if (!tagName.isEmpty() && !value.isEmpty()) {
                tags.put(tagName, value);
            }
            return this;
        }

        /**
         * Add a Map of tags to add to this point.
         *
         * @param tagsToAdd the Map of tags to add
         * @return the Builder instance.
         */
        public Builder tag(final Map<String, String> tagsToAdd) {
            MetricUtil.validateNotNull(tagsToAdd, "tagsToAdd");
            for (Entry<String, String> tag : tagsToAdd.entrySet()) {
                tag(tag.getKey(), tag.getValue());
            }
            return this;
        }

        /**
         * @param field the field name
         * @param value the field value
         * @return the Builder instance.
         */
        public Builder field(final String field, final boolean value) {
            fields.put(field, value);
            return this;
        }

        /**
         * Convenience method equivalent to {@link #field(String, boolean)} but for long value.
         */
        public Builder field(final String field, final long value) {
            fields.put(field, value);
            return this;
        }

        /**
         * Convenience method equivalent to {@link #field(String, boolean)} but for double value.
         */
        public Builder field(final String field, final double value) {
            fields.put(field, value);
            return this;
        }

        /**
         * Convenience method equivalent to {@link #field(String, boolean)} but for Number value.
         */
        public Builder field(final String field, final Number value) {
            fields.put(field, value);
            return this;
        }

        /**
         * Convenience method equivalent to {@link #field(String, boolean)} but for String value.
         */
        public Builder field(final String field, final String value) {
            MetricUtil.validateNotNull(field, "field");
            MetricUtil.validateNotNull(value, "value");
            fields.put(field, value);
            return this;
        }

        /**
         * Add a Map of fields to this point.
         *
         * @param fieldsToAdd the fields to add
         * @return the Builder instance.
         */
        public Builder fields(final Map<String, Object> fieldsToAdd) {
            this.fields.putAll(fieldsToAdd);
            return this;
        }

        /**
         * Add a time to this point.
         *
         * @param timeToSet      the time for this point
         * @param precisionToSet the TimeUnit
         * @return the Builder instance.
         */
        public Builder time(final long timeToSet, final TimeUnit precisionToSet) {
            MetricUtil.validateNotNull(precisionToSet, "precisionToSet");
            this.time = timeToSet;
            this.precision = precisionToSet;
            return this;
        }

        /**
         * Create a new Point.
         *
         * @return the newly created Point.
         */
        public InfluxPoint build() {
            MetricUtil.validateNotNull(this.measurement, "measurement");
            MetricUtil.validateNotEmpty(this.fields, "fields");
            InfluxPoint point = new InfluxPoint();
            point.setMeasurement(this.measurement);
            point.setTags(this.tags);
            point.setFields(this.fields);
            if (this.time != null) {
                point.setTime(this.time);
                point.setPrecision(this.precision);
            }
            return point;
        }
    }

}
