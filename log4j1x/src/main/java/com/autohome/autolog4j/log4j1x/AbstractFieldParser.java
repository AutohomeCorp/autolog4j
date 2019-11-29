package com.autohome.autolog4j.log4j1x;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by kcq on 2017/3/16.
 */
public abstract class AbstractFieldParser {
    public abstract String parse(String fieldName, LoggingEvent loggingEvent);
}
