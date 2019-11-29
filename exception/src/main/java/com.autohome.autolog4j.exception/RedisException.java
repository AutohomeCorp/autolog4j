package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class RedisException extends DependencyException {
    public RedisException(String msg) {
        super(msg);
    }

    public RedisException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.REDIS.getPattern();
    }
}
