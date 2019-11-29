package com.autohome.autolog4j.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;

public abstract class AbstractFieldParser {
    public abstract String parse(String fieldName, ILoggingEvent logEvent);
}
