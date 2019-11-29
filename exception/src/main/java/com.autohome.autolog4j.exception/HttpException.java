package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class HttpException extends DependencyException {
    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.HTTP.getPattern();
    }
}
