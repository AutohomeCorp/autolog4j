package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class KafkaException extends DependencyException {
    public KafkaException(String msg) {
        super(msg);
    }

    public KafkaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KafkaException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.KAFKA.getPattern();
    }
}
