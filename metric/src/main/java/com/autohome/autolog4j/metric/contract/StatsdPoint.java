package com.autohome.autolog4j.metric.contract;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import com.autohome.autolog4j.metric.utils.MetricUtil;


/**
 * Created by NFW on 2018/7/5.
 */
public class StatsdPoint {
    private String measurement;
    private Map<String, String> tags;

    private StatsdPoint() {
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
        builder.append("]");
        return builder.toString();
    }

    /**
     * Builder for a new Point.
     */
    public static final class Builder {
        private final String measurement;
        private final Map<String, String> tags = new TreeMap<>();

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
         * Create a new Point.
         *
         * @return the newly created Point.
         */
        public StatsdPoint build() {
            MetricUtil.validateNotNull(this.measurement, "measurement");
            StatsdPoint point = new StatsdPoint();
            point.setMeasurement(this.measurement);
            point.setTags(this.tags);
            return point;
        }
    }

}
