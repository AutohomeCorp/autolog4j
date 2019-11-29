package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class RabbitMqException extends DependencyException {
    public RabbitMqException(String msg) {
        super(msg);
    }

    public RabbitMqException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RabbitMqException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.RABBITMQ.getPattern();
    }
}
