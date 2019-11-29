package com.autohome.autolog4j.accesslog;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * Created by Menong on 2016/5/31.
 * 日志记录Filter
 */
public class AccessLoggingFilter extends OncePerRequestFilter {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(AccessLoggingFilter.class);
    private static final Logger FILTER_LOGGER = LoggerFactory.getLogger("accessLogger");
    private String separator = "\\t";
    private String placeholder = "-";
    private List<String> contentTypes =
            ImmutableList.of("application/json", "application/xml", "text/plain",
                    "text/xml", "text/html", "image/jpeg", "image/png",
                    "application/vnd.spring-boot.actuator.v1+json");
    private List<String> fields = Arrays.asList(
            LogField.LOG_AT.getName(),
            LogField.Q_TIME.getName(),
            LogField.STATUS.getName(),
            LogField.CLIENT_IP.getName(),
            LogField.HOST.getName(),
            LogField.SERVER_IP.getName(),
            LogField.HTTP_METHOD.getName(),
            LogField.URI_STEM.getName(),
            LogField.QUERY_STRING.getName(),
            LogField.UA.getName(),
            LogField.REFERER.getName(),
            LogField.FORM_STRING.getName(),
            LogField.RESPONSE_CONTENT.getName(),
            LogField.PARENT_TRACE_ID.getName(),
            LogField.TRACE_ID.getName(),
            LogField.X_FORWARDED_FOR.getName());

    //private Consumer<LogBuilder> extendLogger;

    private Map<String, Integer> fieldLengthMap = new HashMap<String, Integer>() {
        {
            put(LogField.RESPONSE_CONTENT.getName(), 0);
            put(LogField.FORM_STRING.getName(), 0);
        }
    };

    private String fixedHost;

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /*public void setExtendLogger(Consumer<LogBuilder> extendLogger) {
        this.extendLogger = extendLogger;
    }*/

    public Map<String, Integer> getFieldLengthMap() {
        return fieldLengthMap;
    }

    public void setFieldLengthMap(Map<String, Integer> fieldLengthMap) {
        this.fieldLengthMap = fieldLengthMap;
    }

    public AccessLoggingFilter() {
        this.separator = "\\t";
    }

    public AccessLoggingFilter(String separator) {
        this.separator = separator;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;

        LogBuilder builder = null;
        if (isFirstRequest) {
            Object traceId = request.getAttribute("trace_id");
            if (traceId == null) {
                request.setAttribute("trace_id", UUID.randomUUID().toString().replace("-", ""));
            }
            request.setAttribute("log_at", DateTime.now().getMillis());

            if (!(request instanceof ContentCachingRequestWrapper)) {
                requestToUse = new ContentCachingRequestWrapper(request);
            }
            if (!(response instanceof ContentCachingResponseWrapper)) {
                responseToUse = new HttpStreamingAwareContentCachingResponseWrapper(response, requestToUse);
            }
        }

        filterChain.doFilter(requestToUse, responseToUse);

        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(responseToUse, ContentCachingResponseWrapper.class);
        if (!isAsyncStarted(request) && responseWrapper != null) {
            builder = new LogBuilder();
            ContentCachingRequestWrapper requestWrapper = WebUtils.getNativeRequest(requestToUse, ContentCachingRequestWrapper.class);
            if (requestWrapper != null && shouldLog(responseToUse)) {
                builder.placeholder(this.placeholder)
                        .separator(this.separator)
                        .host(this.fixedHost)
                        .fieldLengthMap(this.fieldLengthMap)
                        .setRequestAtInMillis((Long) requestToUse.getAttribute("log_at"))
                        .response(responseWrapper)
                        .request(requestWrapper);
                responseWrapper.copyBodyToResponse();
                logger(builder);
            } else {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    /**
     * The default value is "false" so that the filter may log a "before" message
     * at the start of request processing and an "after" message at the end from
     * when the last asynchronously dispatched thread is exiting.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    private boolean shouldLog(HttpServletResponse response) {
        if (response.getStatus() != 200) {
            return true;
        }

        if (Strings.isNullOrEmpty(response.getContentType())) {
            return false;
        }
        for (String item : this.contentTypes) {
            if (response.getContentType().contains(item)) {
                return true;
            }
        }
        return false;
    }

    private void logger(LogBuilder builder) throws IOException {
        if (FILTER_LOGGER.isInfoEnabled()) {
            /*if (extendLogger != null) {
                extendLogger.accept(builder);
            }*/
            String message = builder.build(this.fields);
            try {
                FILTER_LOGGER.info(message);
            } catch (Exception ex) {
                ERROR_LOGGER.error("AccessLoggingFilter -> logger error:" + message, ex);
            }
        }
    }

    public String getFixedHost() {
        return fixedHost;
    }

    public void setFixedHost(String fixedHost) {
        this.fixedHost = fixedHost;
    }

    private static class HttpStreamingAwareContentCachingResponseWrapper extends ContentCachingResponseWrapper {

        private final HttpServletRequest request;

        public HttpStreamingAwareContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            String contentType = this.getContentType();
            if (!Strings.isNullOrEmpty(contentType) && contentType.contains("stream")) {
                return getResponse().getOutputStream();
            } else {
                return super.getOutputStream();
            }
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            String contentType = this.getContentType();
            if (!Strings.isNullOrEmpty(contentType) && contentType.contains("stream")) {
                return getResponse().getWriter();
            } else {
                return super.getWriter();
            }
        }
    }
}
