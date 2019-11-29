package com.autohome.autolog4j.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

public abstract class FillMdcFilter extends OncePerRequestFilter {

    protected abstract void fillMdc(Map<String, String> map,
                                    HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws IOException, ServletException;

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(httpServletRequest);
        if (isFirstRequest) {
            Object traceId = httpServletRequest.getAttribute("trace_id");
            if (traceId == null) {
                httpServletRequest.setAttribute("trace_id", UUID.randomUUID().toString().replace("-", ""));
            }
        }
        HttpServletRequest requestToUse = httpServletRequest;
        if (!(httpServletRequest instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(httpServletRequest);
        }
        Map<String, String> mdcMap = buildMdcMap((ContentCachingRequestWrapper) requestToUse);
        fillMdc(mdcMap, requestToUse, httpServletResponse, filterChain);
    }

    private Map<String, String> buildMdcMap(ContentCachingRequestWrapper requestToUse) {
        Map<String, String> webAttributeMap = new HashMap<>();

        String queryString = requestToUse.getQueryString();
        webAttributeMap.put("UriStem", requestToUse.getRequestURI().toLowerCase());
        webAttributeMap.put("QueryString", queryString == null ? "-" : queryString);
        webAttributeMap.put("ServerIP", requestToUse.getLocalAddr());
        webAttributeMap.put("Host", findHost(requestToUse));
        String formString = getRequestBody(requestToUse);
        webAttributeMap.put("FormString", formString == null ? "-" : formString);
        webAttributeMap.put("TraceId", getTraceId(requestToUse));
        webAttributeMap.put("ContextPath", requestToUse.getContextPath().toLowerCase());
        webAttributeMap.put("UserAgent", getHeader(requestToUse, "User-Agent"));

        return webAttributeMap;
    }

    private String findHost(ContentCachingRequestWrapper requestToUse) {
        if (!Strings.isNullOrEmpty(host)) {
            return host;
        }
        return requestToUse.getServerName().toLowerCase();
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return value == null ? "-" : value;
    }

    private String getTraceId(HttpServletRequest request) {
        String autoRequestId = request.getHeader("auto-request-id");
        if (!Strings.isNullOrEmpty(autoRequestId)) {
            return autoRequestId;
        }
        Object traceId = request.getAttribute("trace_id");
        return traceId == null ? "-" : (String) traceId;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        if (Strings.isNullOrEmpty(request.getContentType())
                || !request.getContentType().contains("json")) {
            return null;
        }

        //如果不调用getParameter函数，request.getContentAsByteArray取不到数据
        if (request.getContentAsByteArray().length == 0) {
            request.getParameter("");
        }

        return bodyContent(request);
    }

    private String bodyContent(ContentCachingRequestWrapper request) {
        byte[] buffer = request.getContentAsByteArray();
        String payload = null;
        if (buffer.length > 0) {
            try {
                payload = new String(buffer, request.getCharacterEncoding());
                if (payload.length() > 2000) {
                    payload = payload.substring(0, 1999);
                }
            } catch (UnsupportedEncodingException e) {
                payload = "[unknown]";
            }
        }
        return payload;
    }
}
