package com.autohome.autolog4j.exception;

/**
 * Created by kcq on 2017/3/14.
 */
public class SolrException extends DependencyException {
    public SolrException(String msg) {
        super(msg);
    }

    public SolrException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SolrException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCategory() {
        return ExceptionCategory.SOLR.getPattern();
    }
}
