package com.autohome.autolog4j.common;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class SimpleLogbackMdcFilter extends OncePerRequestFilter {

    private String host;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(httpServletRequest);
        if (isFirstRequest) {
            Object traceId = httpServletRequest.getAttribute("trace_id");
            if (traceId == null) {
                httpServletRequest.setAttribute("trace_id", UUID.randomUUID().toString().replace("-", ""));
            }
        }

        Map<String, String> mdcMap = buildMdcMap(httpServletRequest);
        try {
            fillMdc(mdcMap);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception ex) {
            MDC.clear();
        }
    }

    private Map<String, String> buildMdcMap(HttpServletRequest requestToUse) {
        Map<String, String> webAttributeMap = new HashMap<>();

        String queryString = requestToUse.getQueryString();
        webAttributeMap.put("UriStem", requestToUse.getRequestURI().toLowerCase());
        webAttributeMap.put("QueryString", queryString == null ? "-" : queryString);
        webAttributeMap.put("ServerIP", requestToUse.getLocalAddr());
        webAttributeMap.put("Host", findHost(requestToUse));
        webAttributeMap.put("TraceId", getTraceId(requestToUse));
        webAttributeMap.put("ContextPath", requestToUse.getContextPath().toLowerCase());
        webAttributeMap.put("UserAgent", getHeader(requestToUse, "User-Agent"));

        return webAttributeMap;
    }

    private String findHost(HttpServletRequest requestToUse) {
        if (!Strings.isNullOrEmpty(host)) {
            return host;
        }
        return requestToUse.getServerName().toLowerCase();
    }

    public void setHost(String host) {
        this.host = host;
    }

    private String getTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute("trace_id");
        return traceId == null ? "-" : (String) traceId;
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return value == null ? "-" : value;
    }

    private void fillMdc(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = MDC.get(entry.getKey());
            if (Strings.isNullOrEmpty(value)) {
                MDC.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
