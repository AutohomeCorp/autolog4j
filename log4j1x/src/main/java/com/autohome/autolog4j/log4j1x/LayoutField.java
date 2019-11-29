package com.autohome.autolog4j.log4j1x;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.autohome.autolog4j.common.JacksonUtil;
import com.autohome.autolog4j.exception.DependencyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;

/**
 * Created by kcq on 2017/3/14.
 */
public class LayoutField {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static String SERVER_IP_ADDR;

    public static final LayoutField CLASS_NAME = create("Class", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return loggingEvent.getLocationInformation().getClassName();
        }
    });

    public static final LayoutField METHOD_NAME = create("Method", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return loggingEvent.getLocationInformation().getMethodName();
        }
    });

    public static final LayoutField LINE = create("Line", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            String lineNumber = loggingEvent.getLocationInformation().getLineNumber();
            return lineNumber.equalsIgnoreCase("?") ? "0" : lineNumber;
        }
    });

    public static final LayoutField EXCEPTION_TYPE = create("ExceptionType", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getThrowableInformation() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation().getThrowable() == null) {
                return null;
            }
            Throwable ex = loggingEvent.getThrowableInformation().getThrowable();
            if (ex instanceof DependencyException && ex.getCause() != null) {
                return ((DependencyException) ex).getCause().getClass().getCanonicalName();
            }
            return ex.getClass().getCanonicalName();
        }
    });

    public static final LayoutField EXCEPTION_MESSAGE = create("ExceptionMessage", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getThrowableInformation() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation().getThrowable() == null) {
                return null;
            }
            return loggingEvent.getThrowableInformation().getThrowable().getMessage();
        }
    });

    public static final LayoutField STACK_TRACE = create("StackTrace", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getThrowableStrRep() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation().getThrowable() == null) {
                return null;
            }
            StringBuilderWriter sbw = new StringBuilderWriter();
            try (PrintWriter pw = new PrintWriter(sbw)) {
                loggingEvent.getThrowableInformation().getThrowable().printStackTrace(pw);
                return sbw.toString();
            }
        }
    });

    public static final LayoutField MESSAGE = create("CustomMessage", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return loggingEvent.getRenderedMessage();
        }
    });

    public static final LayoutField LOGGER = create("Logger", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return loggingEvent.getLoggerName();
        }
    });

    public static final LayoutField IO_TYPE = create("IOType", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getThrowableInformation() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation().getThrowable() == null) {
                return null;
            }
            Throwable ex = loggingEvent.getThrowableInformation().getThrowable();
            if (ex instanceof DependencyException) {
                return ((DependencyException) ex).getCategory();
            }
            return "unknown";
        }
    });

    public static final LayoutField LOG_AT = create("LogAt", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return new DateTime(loggingEvent.timeStamp).toDateTimeISO().toString();
        }
    });

    public static final LayoutField TRACE_ID = create("TraceId", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getMDC("X-B3-TraceId") != null) {
                return (String) loggingEvent.getMDC("X-B3-TraceId");
            }
            if (loggingEvent.getMDC("tid") != null) {
                //skywalking traceid
                return loggingEvent.getMDC("tid").toString();
            }
            if (loggingEvent.getMDC(fieldName) != null) {
                return loggingEvent.getMDC(fieldName).toString();
            }
            return null;
        }
    });

    public static final LayoutField HOST = create("Host", MdcFieldParser.INSTANCE);

    public static final LayoutField SERVER_IP = create("ServerIP", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getMDC(fieldName) == null) {
                return LayoutField.findServerIP();
            }
            return loggingEvent.getMDC(fieldName).toString();
        }
    });

    public static final LayoutField URI_STEM = create("UriStem", MdcFieldParser.INSTANCE);

    public static final LayoutField QUERY_STRING = create("QueryString", MdcFieldParser.INSTANCE);

    public static final LayoutField FORM_STRING = create("FormString", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getLevel().toInt() < Level.WARN.toInt()
                    || loggingEvent.getMDC("FormString") == null) {
                return null;
            }
            return loggingEvent.getMDC("FormString").toString();
        }
    });

    public static final LayoutField LEVEL = create("Level", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            return loggingEvent.getLevel().toString().toUpperCase();
        }
    });

    public static final LayoutField CONTEXT_PATH = create("ContextPath", MdcFieldParser.INSTANCE);

    public static final LayoutField METHOD_PARAMS = create("MethodParams", new AbstractFieldParser() {
        @Override
        public String parse(String fieldName, LoggingEvent loggingEvent) {
            if (loggingEvent.getThrowableInformation() == null) {
                return null;
            }
            if (loggingEvent.getThrowableInformation().getThrowable() == null) {
                return null;
            }
            Throwable ex = loggingEvent.getThrowableInformation().getThrowable();
            if (ex instanceof DependencyException) {
                Object[] args = ((DependencyException) ex).getArgs();
                if (args == null || args.length == 0) {
                    return null;
                }

                return JacksonUtil.serialize(args);
            }
            return null;
        }
    });

    public static final LayoutField USER_AGENT = create("UserAgent", MdcFieldParser.INSTANCE);

    private String name;
    private String value;
    private AbstractFieldParser fieldParser;

    public String getName() {
        return this.name;
    }

    public static LayoutField create(String name, AbstractFieldParser fieldParser) {
        return new LayoutField(name, fieldParser);
    }

    public LayoutField(String name, AbstractFieldParser fieldParser) {
        this.name = name;
        this.fieldParser = fieldParser;
    }

    public LayoutField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String format(LoggingEvent loggingEvent) {
        if (!Strings.isNullOrEmpty(this.value)) {
            return this.value;
        }
        if (this.fieldParser == null) {
            return null;
        }
        return this.fieldParser.parse(this.name, loggingEvent);
    }

    public static String findServerIP() {
        if (Strings.isNullOrEmpty(SERVER_IP_ADDR)) {
            try {
                SERVER_IP_ADDR = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return null;
            }
        }
        return SERVER_IP_ADDR;
    }
}
