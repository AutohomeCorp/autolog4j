package com.autohome.autolog4j.log4j1x;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by kcq on 2017/3/14.
 */
public class MdcFieldParser extends AbstractFieldParser {
    public static final MdcFieldParser INSTANCE = new MdcFieldParser();

    @Override
    public String parse(String fieldName, LoggingEvent loggingEvent) {
        if (loggingEvent.getMDC(fieldName) == null) {
            return null;
        }
        return loggingEvent.getMDC(fieldName).toString();
    }
}
