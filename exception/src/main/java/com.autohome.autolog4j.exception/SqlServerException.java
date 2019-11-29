package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class SqlServerException extends DependencyException {
    public SqlServerException(String msg) {
        super(msg);
    }

    public SqlServerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SqlServerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.SQLSERVER.getPattern();
    }
}
