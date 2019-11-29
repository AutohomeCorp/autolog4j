package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class HBaseException extends DependencyException {
    public HBaseException(String msg) {
        super(msg);
    }

    public HBaseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HBaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.HBASE.getPattern();
    }
}
