package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/13.
 */
public class MysqlException extends DependencyException {
    public MysqlException(String msg) {
        super(msg);
    }

    public MysqlException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MysqlException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.MYSQL.getPattern();
    }
}
