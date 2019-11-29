package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/14.
 */
public class MongodbException extends DependencyException {
    public MongodbException(String msg) {
        super(msg);
    }

    public MongodbException(String msg, Throwable cause) {
        super(msg);
    }

    public MongodbException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.MONGODB.getPattern();
    }
}
