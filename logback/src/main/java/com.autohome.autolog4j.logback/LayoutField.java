package com.autohome.autolog4j.logback;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.autohome.autolog4j.common.FieldName;
import com.autohome.autolog4j.common.JacksonUtil;
import com.google.common.base.Strings;
import org.joda.time.DateTime;

public class LayoutField {
    private static String LOCAL_IP_ADDR;

    private static ThrowableProxyConverter throwableProxyConverter = new ThrowableProxyConverter();

    private String name;
    private String value;
    private AbstractFieldParser fieldParser;

    static {
        throwableProxyConverter.start();
    }

    public LayoutField(String name, AbstractFieldParser parser) {
        this.name = name;
        this.fieldParser = parser;
    }

    public LayoutField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String format(ILoggingEvent loggingEvent) {
        if (!Strings.isNullOrEmpty(this.value)) {
            return this.value;
        }

        if (this.fieldParser == null) {
            return null;
        }
        return this.fieldParser.parse(name, loggingEvent);
    }

    public static LayoutField create(String name, AbstractFieldParser fieldParser) {
        return new LayoutField(name, fieldParser);
    }

    public static final LayoutField LOG_AT = create(FieldName.LOG_AT, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return (new DateTime(logEvent.getTimeStamp())).toDateTimeISO().toString();
        }
    });

    public static String findServerIP() {
        if (Strings.isNullOrEmpty(LOCAL_IP_ADDR)) {
            try {
                LOCAL_IP_ADDR = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return null;
            }
        }
        return LOCAL_IP_ADDR;
    }

    public static final LayoutField CLASS_NAME = create(FieldName.CLASS_NAME, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.hasCallerData()) {
                return logEvent.getCallerData()[0].getClassName();
            }

            return null;
        }
    });

    public static final LayoutField LINE = create(FieldName.LINE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.hasCallerData()) {
                return logEvent.getCallerData()[0].getLineNumber() + "";
            }

            return "0";
        }
    });

    public static final LayoutField METHOD_NAME = create(FieldName.METHOD_NAME, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.hasCallerData()) {
                return logEvent.getCallerData()[0].getMethodName();
            }

            return null;
        }
    });

    public static final LayoutField EXCEPTION_TYPE = create(FieldName.EXCEPTION_TYPE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            IThrowableProxy throwableProxy = logEvent.getThrowableProxy();

            if (throwableProxy == null) {
                return null;
            }
            return throwableProxy.getClassName();
        }
    });

    public static final LayoutField EXCEPTION_MESSAGE = create(FieldName.EXCEPTION_MESSAGE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            IThrowableProxy throwableProxy = logEvent.getThrowableProxy();

            if (throwableProxy == null) {
                return null;
            }

            return throwableProxy.getMessage();
        }
    });

    public static final LayoutField STACK_TRACE = create(FieldName.STACK_TRACE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            IThrowableProxy throwableProxy = logEvent.getThrowableProxy();

            if (throwableProxy == null) {
                return null;
            }

            String stackTrace = throwableProxyConverter.convert(logEvent);
            return stackTrace;
        }
    });

    public static final LayoutField CUSTOM_MESSAGE = create(FieldName.CUSTOM_MESSAGE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return logEvent.getFormattedMessage();
        }
    });

    public static final LayoutField LOGGER = create(FieldName.LOGGER, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return logEvent.getLoggerName();
        }
    });

    public static final LayoutField IO_TYPE = create(FieldName.IO_TYPE, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return null;
        }
    });

    public static final LayoutField TRACE_ID = create(FieldName.TRACE_ID, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey("X-B3-TraceId")) {
                //zipkin traceid
                return logEvent.getMDCPropertyMap().get("X-B3-TraceId");
            }
            if (logEvent.getMDCPropertyMap().containsKey("tid")) {
                //skywalking traceid
                return logEvent.getMDCPropertyMap().get("tid");
            }
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return null;
        }
    });

    public static final LayoutField HOST = create(FieldName.HOST, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return null;
        }
    });

    public static final LayoutField SERVER_IP = create(FieldName.SERVER_IP, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return findServerIP();
        }
    });

    public static final LayoutField URI_STEM = create(FieldName.URI_STEM, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return null;
        }
    });

    public static final LayoutField QUERY_STRING = create(FieldName.QUERY_STRING, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return null;
        }
    });

    public static final LayoutField FORM_STRING = create(FieldName.FORM_STRING, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return null;
        }
    });

    public static final LayoutField LEVEL = create(FieldName.LEVEL, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return logEvent.getLevel().levelStr.toUpperCase();
        }
    });

    public static final LayoutField CONTEXT_PATH = create(FieldName.CONTEXT_PATH, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            return null;
        }
    });

    public static final LayoutField METHOD_PARAMS = create(FieldName.METHOD_PARAMS, new AbstractFieldParser() {

        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getArgumentArray() != null) {
                return JacksonUtil.serialize(logEvent.getArgumentArray());
            }
            return null;
        }
    });

    public static final LayoutField USER_AGENT = create(FieldName.USER_AGENT, new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, ILoggingEvent logEvent) {
            if (logEvent.getMDCPropertyMap().containsKey(fieldName)) {
                return logEvent.getMDCPropertyMap().get(fieldName);
            }
            return null;
        }
    });
}
