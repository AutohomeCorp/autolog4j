package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/14.
 */
public class ElasticsearchException extends DependencyException {
    public ElasticsearchException(String msg) {
        super(msg);
    }

    public ElasticsearchException(String msg, Throwable cause) {
        super(msg);
    }

    public ElasticsearchException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.ELASTICSEARCH.getPattern();
    }
}
