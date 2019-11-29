package com.autohome.autolog4j.log4j2x;

import org.apache.logging.log4j.core.LogEvent;

/**
 * Created by kcq on 2017/6/8.
 */
public abstract class AbstractFieldParser {
    public abstract String parse(String fieldName, LogEvent logEvent);
}
