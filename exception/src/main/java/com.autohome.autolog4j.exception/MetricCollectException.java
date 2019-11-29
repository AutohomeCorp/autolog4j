package com.autohome.autolog4j.exception;

/**
 * Created by NFW on 2018/5/8.
 */
public class MetricCollectException extends DependencyException {

    public MetricCollectException(String msg) {
        super(msg);
    }

    public MetricCollectException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MetricCollectException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.METRICCOLLECT.getPattern();
    }
}
