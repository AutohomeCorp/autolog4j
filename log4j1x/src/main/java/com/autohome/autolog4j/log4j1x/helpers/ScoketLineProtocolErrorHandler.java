package com.autohome.autolog4j.log4j1x.helpers;

import java.io.InterruptedIOException;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by NFW on 2018/4/19.
 */
public class ScoketLineProtocolErrorHandler implements ErrorHandler {

    boolean ignoreExceptions = true;

    public ScoketLineProtocolErrorHandler(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }

    /**
     * Does not do anything.
     */
    @Override
    public void setLogger(Logger logger) {
    }


    /**
     * No options to activate.
     */
    @Override
    public void activateOptions() {
    }


    /**
     * Prints the message and the stack trace of the exception on
     * <code>System.err</code>.
     */
    @Override
    public void error(String message, Exception e, int errorCode) {
        error(message, e, errorCode, null);
    }

    /**
     * Prints the message and the stack trace of the exception on
     * <code>System.err</code>.
     */
    @Override
    public void error(String message, Exception e, int errorCode, LoggingEvent event) {
        if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        if (!isIgnoreExceptions()) {
            throw new RuntimeException(message, e);
        }
    }


    /**
     * Print a the error message passed as parameter on
     * <code>System.err</code>.
     */
    @Override
    public void error(String message) {
        //LogLog.error(message);
        if (!isIgnoreExceptions()) {
            throw new RuntimeException(message);
        }
    }

    /**
     * Does not do anything.
     */
    @Override
    public void setAppender(Appender appender) {
    }

    /**
     * Does not do anything.
     */
    @Override
    public void setBackupAppender(Appender appender) {
    }
}
