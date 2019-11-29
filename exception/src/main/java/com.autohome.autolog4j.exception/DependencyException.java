package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/14.
 */
public abstract class DependencyException extends RuntimeException {
    public DependencyException(String msg) {
        super(msg);
    }

    public DependencyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DependencyException(Throwable cause) {
        super(cause);
    }

    private Object[] args;

    public abstract String getCategory();

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
